import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import HospitalFeeResolve from './route/hospital-fee-routing-resolve.service';

const hospitalFeeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/hospital-fee.component').then(m => m.HospitalFeeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/hospital-fee-detail.component').then(m => m.HospitalFeeDetailComponent),
    resolve: {
      hospitalFee: HospitalFeeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/hospital-fee-update.component').then(m => m.HospitalFeeUpdateComponent),
    resolve: {
      hospitalFee: HospitalFeeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/hospital-fee-update.component').then(m => m.HospitalFeeUpdateComponent),
    resolve: {
      hospitalFee: HospitalFeeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default hospitalFeeRoute;
