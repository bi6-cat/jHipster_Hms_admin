import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPrescription, NewPrescription } from '../prescription.model';

export type PartialUpdatePrescription = Partial<IPrescription> & Pick<IPrescription, 'id'>;

export type EntityResponseType = HttpResponse<IPrescription>;
export type EntityArrayResponseType = HttpResponse<IPrescription[]>;

@Injectable({ providedIn: 'root' })
export class PrescriptionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/prescriptions');

  create(prescription: NewPrescription): Observable<EntityResponseType> {
    return this.http.post<IPrescription>(this.resourceUrl, prescription, { observe: 'response' });
  }

  update(prescription: IPrescription): Observable<EntityResponseType> {
    return this.http.put<IPrescription>(`${this.resourceUrl}/${this.getPrescriptionIdentifier(prescription)}`, prescription, {
      observe: 'response',
    });
  }

  partialUpdate(prescription: PartialUpdatePrescription): Observable<EntityResponseType> {
    return this.http.patch<IPrescription>(`${this.resourceUrl}/${this.getPrescriptionIdentifier(prescription)}`, prescription, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPrescription>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrescription[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPrescriptionIdentifier(prescription: Pick<IPrescription, 'id'>): number {
    return prescription.id;
  }

  comparePrescription(o1: Pick<IPrescription, 'id'> | null, o2: Pick<IPrescription, 'id'> | null): boolean {
    return o1 && o2 ? this.getPrescriptionIdentifier(o1) === this.getPrescriptionIdentifier(o2) : o1 === o2;
  }

  addPrescriptionToCollectionIfMissing<Type extends Pick<IPrescription, 'id'>>(
    prescriptionCollection: Type[],
    ...prescriptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const prescriptions: Type[] = prescriptionsToCheck.filter(isPresent);
    if (prescriptions.length > 0) {
      const prescriptionCollectionIdentifiers = prescriptionCollection.map(prescriptionItem =>
        this.getPrescriptionIdentifier(prescriptionItem),
      );
      const prescriptionsToAdd = prescriptions.filter(prescriptionItem => {
        const prescriptionIdentifier = this.getPrescriptionIdentifier(prescriptionItem);
        if (prescriptionCollectionIdentifiers.includes(prescriptionIdentifier)) {
          return false;
        }
        prescriptionCollectionIdentifiers.push(prescriptionIdentifier);
        return true;
      });
      return [...prescriptionsToAdd, ...prescriptionCollection];
    }
    return prescriptionCollection;
  }
}
