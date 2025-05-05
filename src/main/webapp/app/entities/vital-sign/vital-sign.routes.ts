import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import VitalSignResolve from './route/vital-sign-routing-resolve.service';

const vitalSignRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/vital-sign.component').then(m => m.VitalSignComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/vital-sign-detail.component').then(m => m.VitalSignDetailComponent),
    resolve: {
      vitalSign: VitalSignResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/vital-sign-update.component').then(m => m.VitalSignUpdateComponent),
    resolve: {
      vitalSign: VitalSignResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/vital-sign-update.component').then(m => m.VitalSignUpdateComponent),
    resolve: {
      vitalSign: VitalSignResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default vitalSignRoute;
