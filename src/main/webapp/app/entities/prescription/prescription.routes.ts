import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PrescriptionResolve from './route/prescription-routing-resolve.service';

const prescriptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/prescription.component').then(m => m.PrescriptionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/prescription-detail.component').then(m => m.PrescriptionDetailComponent),
    resolve: {
      prescription: PrescriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/prescription-update.component').then(m => m.PrescriptionUpdateComponent),
    resolve: {
      prescription: PrescriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/prescription-update.component').then(m => m.PrescriptionUpdateComponent),
    resolve: {
      prescription: PrescriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default prescriptionRoute;
