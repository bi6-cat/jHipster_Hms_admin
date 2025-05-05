import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'hospitalApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'patient',
    data: { pageTitle: 'hospitalApp.patient.home.title' },
    loadChildren: () => import('./patient/patient.routes'),
  },
  {
    path: 'doctor',
    data: { pageTitle: 'hospitalApp.doctor.home.title' },
    loadChildren: () => import('./doctor/doctor.routes'),
  },
  {
    path: 'appointment',
    data: { pageTitle: 'hospitalApp.appointment.home.title' },
    loadChildren: () => import('./appointment/appointment.routes'),
  },
  {
    path: 'vital-sign',
    data: { pageTitle: 'hospitalApp.vitalSign.home.title' },
    loadChildren: () => import('./vital-sign/vital-sign.routes'),
  },
  {
    path: 'disease',
    data: { pageTitle: 'hospitalApp.disease.home.title' },
    loadChildren: () => import('./disease/disease.routes'),
  },
  {
    path: 'treatment',
    data: { pageTitle: 'hospitalApp.treatment.home.title' },
    loadChildren: () => import('./treatment/treatment.routes'),
  },
  {
    path: 'hospital-fee',
    data: { pageTitle: 'hospitalApp.hospitalFee.home.title' },
    loadChildren: () => import('./hospital-fee/hospital-fee.routes'),
  },
  {
    path: 'prescription',
    data: { pageTitle: 'hospitalApp.prescription.home.title' },
    loadChildren: () => import('./prescription/prescription.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
