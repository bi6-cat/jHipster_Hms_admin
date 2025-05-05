import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVitalSign, NewVitalSign } from '../vital-sign.model';

export type PartialUpdateVitalSign = Partial<IVitalSign> & Pick<IVitalSign, 'id'>;

type RestOf<T extends IVitalSign | NewVitalSign> = Omit<T, 'measurementDate'> & {
  measurementDate?: string | null;
};

export type RestVitalSign = RestOf<IVitalSign>;

export type NewRestVitalSign = RestOf<NewVitalSign>;

export type PartialUpdateRestVitalSign = RestOf<PartialUpdateVitalSign>;

export type EntityResponseType = HttpResponse<IVitalSign>;
export type EntityArrayResponseType = HttpResponse<IVitalSign[]>;

@Injectable({ providedIn: 'root' })
export class VitalSignService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/vital-signs');

  create(vitalSign: NewVitalSign): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vitalSign);
    return this.http
      .post<RestVitalSign>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(vitalSign: IVitalSign): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vitalSign);
    return this.http
      .put<RestVitalSign>(`${this.resourceUrl}/${this.getVitalSignIdentifier(vitalSign)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(vitalSign: PartialUpdateVitalSign): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vitalSign);
    return this.http
      .patch<RestVitalSign>(`${this.resourceUrl}/${this.getVitalSignIdentifier(vitalSign)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestVitalSign>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestVitalSign[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getVitalSignIdentifier(vitalSign: Pick<IVitalSign, 'id'>): number {
    return vitalSign.id;
  }

  compareVitalSign(o1: Pick<IVitalSign, 'id'> | null, o2: Pick<IVitalSign, 'id'> | null): boolean {
    return o1 && o2 ? this.getVitalSignIdentifier(o1) === this.getVitalSignIdentifier(o2) : o1 === o2;
  }

  addVitalSignToCollectionIfMissing<Type extends Pick<IVitalSign, 'id'>>(
    vitalSignCollection: Type[],
    ...vitalSignsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const vitalSigns: Type[] = vitalSignsToCheck.filter(isPresent);
    if (vitalSigns.length > 0) {
      const vitalSignCollectionIdentifiers = vitalSignCollection.map(vitalSignItem => this.getVitalSignIdentifier(vitalSignItem));
      const vitalSignsToAdd = vitalSigns.filter(vitalSignItem => {
        const vitalSignIdentifier = this.getVitalSignIdentifier(vitalSignItem);
        if (vitalSignCollectionIdentifiers.includes(vitalSignIdentifier)) {
          return false;
        }
        vitalSignCollectionIdentifiers.push(vitalSignIdentifier);
        return true;
      });
      return [...vitalSignsToAdd, ...vitalSignCollection];
    }
    return vitalSignCollection;
  }

  protected convertDateFromClient<T extends IVitalSign | NewVitalSign | PartialUpdateVitalSign>(vitalSign: T): RestOf<T> {
    return {
      ...vitalSign,
      measurementDate: vitalSign.measurementDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restVitalSign: RestVitalSign): IVitalSign {
    return {
      ...restVitalSign,
      measurementDate: restVitalSign.measurementDate ? dayjs(restVitalSign.measurementDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestVitalSign>): HttpResponse<IVitalSign> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestVitalSign[]>): HttpResponse<IVitalSign[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
