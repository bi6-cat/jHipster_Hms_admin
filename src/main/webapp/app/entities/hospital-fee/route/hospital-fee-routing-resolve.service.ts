import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHospitalFee } from '../hospital-fee.model';
import { HospitalFeeService } from '../service/hospital-fee.service';

const hospitalFeeResolve = (route: ActivatedRouteSnapshot): Observable<null | IHospitalFee> => {
  const id = route.params.id;
  if (id) {
    return inject(HospitalFeeService)
      .find(id)
      .pipe(
        mergeMap((hospitalFee: HttpResponse<IHospitalFee>) => {
          if (hospitalFee.body) {
            return of(hospitalFee.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default hospitalFeeResolve;
