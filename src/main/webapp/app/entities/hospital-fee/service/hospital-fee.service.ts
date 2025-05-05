import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHospitalFee, NewHospitalFee } from '../hospital-fee.model';

export type PartialUpdateHospitalFee = Partial<IHospitalFee> & Pick<IHospitalFee, 'id'>;

type RestOf<T extends IHospitalFee | NewHospitalFee> = Omit<T, 'feeDate'> & {
  feeDate?: string | null;
};

export type RestHospitalFee = RestOf<IHospitalFee>;

export type NewRestHospitalFee = RestOf<NewHospitalFee>;

export type PartialUpdateRestHospitalFee = RestOf<PartialUpdateHospitalFee>;

export type EntityResponseType = HttpResponse<IHospitalFee>;
export type EntityArrayResponseType = HttpResponse<IHospitalFee[]>;

@Injectable({ providedIn: 'root' })
export class HospitalFeeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/hospital-fees');

  create(hospitalFee: NewHospitalFee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(hospitalFee);
    return this.http
      .post<RestHospitalFee>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(hospitalFee: IHospitalFee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(hospitalFee);
    return this.http
      .put<RestHospitalFee>(`${this.resourceUrl}/${this.getHospitalFeeIdentifier(hospitalFee)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(hospitalFee: PartialUpdateHospitalFee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(hospitalFee);
    return this.http
      .patch<RestHospitalFee>(`${this.resourceUrl}/${this.getHospitalFeeIdentifier(hospitalFee)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestHospitalFee>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestHospitalFee[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHospitalFeeIdentifier(hospitalFee: Pick<IHospitalFee, 'id'>): number {
    return hospitalFee.id;
  }

  compareHospitalFee(o1: Pick<IHospitalFee, 'id'> | null, o2: Pick<IHospitalFee, 'id'> | null): boolean {
    return o1 && o2 ? this.getHospitalFeeIdentifier(o1) === this.getHospitalFeeIdentifier(o2) : o1 === o2;
  }

  addHospitalFeeToCollectionIfMissing<Type extends Pick<IHospitalFee, 'id'>>(
    hospitalFeeCollection: Type[],
    ...hospitalFeesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const hospitalFees: Type[] = hospitalFeesToCheck.filter(isPresent);
    if (hospitalFees.length > 0) {
      const hospitalFeeCollectionIdentifiers = hospitalFeeCollection.map(hospitalFeeItem => this.getHospitalFeeIdentifier(hospitalFeeItem));
      const hospitalFeesToAdd = hospitalFees.filter(hospitalFeeItem => {
        const hospitalFeeIdentifier = this.getHospitalFeeIdentifier(hospitalFeeItem);
        if (hospitalFeeCollectionIdentifiers.includes(hospitalFeeIdentifier)) {
          return false;
        }
        hospitalFeeCollectionIdentifiers.push(hospitalFeeIdentifier);
        return true;
      });
      return [...hospitalFeesToAdd, ...hospitalFeeCollection];
    }
    return hospitalFeeCollection;
  }

  protected convertDateFromClient<T extends IHospitalFee | NewHospitalFee | PartialUpdateHospitalFee>(hospitalFee: T): RestOf<T> {
    return {
      ...hospitalFee,
      feeDate: hospitalFee.feeDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restHospitalFee: RestHospitalFee): IHospitalFee {
    return {
      ...restHospitalFee,
      feeDate: restHospitalFee.feeDate ? dayjs(restHospitalFee.feeDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestHospitalFee>): HttpResponse<IHospitalFee> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestHospitalFee[]>): HttpResponse<IHospitalFee[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
