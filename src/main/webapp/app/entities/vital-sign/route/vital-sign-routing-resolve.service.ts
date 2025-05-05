import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVitalSign } from '../vital-sign.model';
import { VitalSignService } from '../service/vital-sign.service';

const vitalSignResolve = (route: ActivatedRouteSnapshot): Observable<null | IVitalSign> => {
  const id = route.params.id;
  if (id) {
    return inject(VitalSignService)
      .find(id)
      .pipe(
        mergeMap((vitalSign: HttpResponse<IVitalSign>) => {
          if (vitalSign.body) {
            return of(vitalSign.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default vitalSignResolve;
