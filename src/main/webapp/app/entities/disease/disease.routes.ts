import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DiseaseResolve from './route/disease-routing-resolve.service';

const diseaseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/disease.component').then(m => m.DiseaseComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/disease-detail.component').then(m => m.DiseaseDetailComponent),
    resolve: {
      disease: DiseaseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/disease-update.component').then(m => m.DiseaseUpdateComponent),
    resolve: {
      disease: DiseaseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/disease-update.component').then(m => m.DiseaseUpdateComponent),
    resolve: {
      disease: DiseaseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default diseaseRoute;
