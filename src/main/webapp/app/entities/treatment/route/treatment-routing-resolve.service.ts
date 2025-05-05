import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITreatment } from '../treatment.model';
import { TreatmentService } from '../service/treatment.service';

const treatmentResolve = (route: ActivatedRouteSnapshot): Observable<null | ITreatment> => {
  const id = route.params.id;
  if (id) {
    return inject(TreatmentService)
      .find(id)
      .pipe(
        mergeMap((treatment: HttpResponse<ITreatment>) => {
          if (treatment.body) {
            return of(treatment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default treatmentResolve;
