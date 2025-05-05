import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDisease, NewDisease } from '../disease.model';

export type PartialUpdateDisease = Partial<IDisease> & Pick<IDisease, 'id'>;

type RestOf<T extends IDisease | NewDisease> = Omit<T, 'diagnosisDate'> & {
  diagnosisDate?: string | null;
};

export type RestDisease = RestOf<IDisease>;

export type NewRestDisease = RestOf<NewDisease>;

export type PartialUpdateRestDisease = RestOf<PartialUpdateDisease>;

export type EntityResponseType = HttpResponse<IDisease>;
export type EntityArrayResponseType = HttpResponse<IDisease[]>;

@Injectable({ providedIn: 'root' })
export class DiseaseService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/diseases');

  create(disease: NewDisease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(disease);
    return this.http
      .post<RestDisease>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(disease: IDisease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(disease);
    return this.http
      .put<RestDisease>(`${this.resourceUrl}/${this.getDiseaseIdentifier(disease)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(disease: PartialUpdateDisease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(disease);
    return this.http
      .patch<RestDisease>(`${this.resourceUrl}/${this.getDiseaseIdentifier(disease)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDisease>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDisease[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDiseaseIdentifier(disease: Pick<IDisease, 'id'>): number {
    return disease.id;
  }

  compareDisease(o1: Pick<IDisease, 'id'> | null, o2: Pick<IDisease, 'id'> | null): boolean {
    return o1 && o2 ? this.getDiseaseIdentifier(o1) === this.getDiseaseIdentifier(o2) : o1 === o2;
  }

  addDiseaseToCollectionIfMissing<Type extends Pick<IDisease, 'id'>>(
    diseaseCollection: Type[],
    ...diseasesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const diseases: Type[] = diseasesToCheck.filter(isPresent);
    if (diseases.length > 0) {
      const diseaseCollectionIdentifiers = diseaseCollection.map(diseaseItem => this.getDiseaseIdentifier(diseaseItem));
      const diseasesToAdd = diseases.filter(diseaseItem => {
        const diseaseIdentifier = this.getDiseaseIdentifier(diseaseItem);
        if (diseaseCollectionIdentifiers.includes(diseaseIdentifier)) {
          return false;
        }
        diseaseCollectionIdentifiers.push(diseaseIdentifier);
        return true;
      });
      return [...diseasesToAdd, ...diseaseCollection];
    }
    return diseaseCollection;
  }

  protected convertDateFromClient<T extends IDisease | NewDisease | PartialUpdateDisease>(disease: T): RestOf<T> {
    return {
      ...disease,
      diagnosisDate: disease.diagnosisDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDisease: RestDisease): IDisease {
    return {
      ...restDisease,
      diagnosisDate: restDisease.diagnosisDate ? dayjs(restDisease.diagnosisDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDisease>): HttpResponse<IDisease> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDisease[]>): HttpResponse<IDisease[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
