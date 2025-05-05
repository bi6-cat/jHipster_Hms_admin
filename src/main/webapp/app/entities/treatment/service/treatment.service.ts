import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITreatment, NewTreatment } from '../treatment.model';

export type PartialUpdateTreatment = Partial<ITreatment> & Pick<ITreatment, 'id'>;

type RestOf<T extends ITreatment | NewTreatment> = Omit<T, 'treatmentDate'> & {
  treatmentDate?: string | null;
};

export type RestTreatment = RestOf<ITreatment>;

export type NewRestTreatment = RestOf<NewTreatment>;

export type PartialUpdateRestTreatment = RestOf<PartialUpdateTreatment>;

export type EntityResponseType = HttpResponse<ITreatment>;
export type EntityArrayResponseType = HttpResponse<ITreatment[]>;

@Injectable({ providedIn: 'root' })
export class TreatmentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/treatments');

  create(treatment: NewTreatment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(treatment);
    return this.http
      .post<RestTreatment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(treatment: ITreatment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(treatment);
    return this.http
      .put<RestTreatment>(`${this.resourceUrl}/${this.getTreatmentIdentifier(treatment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(treatment: PartialUpdateTreatment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(treatment);
    return this.http
      .patch<RestTreatment>(`${this.resourceUrl}/${this.getTreatmentIdentifier(treatment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTreatment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTreatment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTreatmentIdentifier(treatment: Pick<ITreatment, 'id'>): number {
    return treatment.id;
  }

  compareTreatment(o1: Pick<ITreatment, 'id'> | null, o2: Pick<ITreatment, 'id'> | null): boolean {
    return o1 && o2 ? this.getTreatmentIdentifier(o1) === this.getTreatmentIdentifier(o2) : o1 === o2;
  }

  addTreatmentToCollectionIfMissing<Type extends Pick<ITreatment, 'id'>>(
    treatmentCollection: Type[],
    ...treatmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const treatments: Type[] = treatmentsToCheck.filter(isPresent);
    if (treatments.length > 0) {
      const treatmentCollectionIdentifiers = treatmentCollection.map(treatmentItem => this.getTreatmentIdentifier(treatmentItem));
      const treatmentsToAdd = treatments.filter(treatmentItem => {
        const treatmentIdentifier = this.getTreatmentIdentifier(treatmentItem);
        if (treatmentCollectionIdentifiers.includes(treatmentIdentifier)) {
          return false;
        }
        treatmentCollectionIdentifiers.push(treatmentIdentifier);
        return true;
      });
      return [...treatmentsToAdd, ...treatmentCollection];
    }
    return treatmentCollection;
  }

  protected convertDateFromClient<T extends ITreatment | NewTreatment | PartialUpdateTreatment>(treatment: T): RestOf<T> {
    return {
      ...treatment,
      treatmentDate: treatment.treatmentDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restTreatment: RestTreatment): ITreatment {
    return {
      ...restTreatment,
      treatmentDate: restTreatment.treatmentDate ? dayjs(restTreatment.treatmentDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTreatment>): HttpResponse<ITreatment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTreatment[]>): HttpResponse<ITreatment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
