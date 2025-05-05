import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TreatmentResolve from './route/treatment-routing-resolve.service';

const treatmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/treatment.component').then(m => m.TreatmentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/treatment-detail.component').then(m => m.TreatmentDetailComponent),
    resolve: {
      treatment: TreatmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/treatment-update.component').then(m => m.TreatmentUpdateComponent),
    resolve: {
      treatment: TreatmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/treatment-update.component').then(m => m.TreatmentUpdateComponent),
    resolve: {
      treatment: TreatmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default treatmentRoute;
