import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDisease } from '../disease.model';
import { DiseaseService } from '../service/disease.service';

const diseaseResolve = (route: ActivatedRouteSnapshot): Observable<null | IDisease> => {
  const id = route.params.id;
  if (id) {
    return inject(DiseaseService)
      .find(id)
      .pipe(
        mergeMap((disease: HttpResponse<IDisease>) => {
          if (disease.body) {
            return of(disease.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default diseaseResolve;
