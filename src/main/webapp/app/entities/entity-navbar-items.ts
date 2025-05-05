import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Patient',
    route: '/patient',
    translationKey: 'global.menu.entities.patient',
  },
  {
    name: 'Doctor',
    route: '/doctor',
    translationKey: 'global.menu.entities.doctor',
  },
  {
    name: 'Appointment',
    route: '/appointment',
    translationKey: 'global.menu.entities.appointment',
  },
  {
    name: 'VitalSign',
    route: '/vital-sign',
    translationKey: 'global.menu.entities.vitalSign',
  },
  {
    name: 'Disease',
    route: '/disease',
    translationKey: 'global.menu.entities.disease',
  },
  {
    name: 'Treatment',
    route: '/treatment',
    translationKey: 'global.menu.entities.treatment',
  },
  {
    name: 'HospitalFee',
    route: '/hospital-fee',
    translationKey: 'global.menu.entities.hospitalFee',
  },
  {
    name: 'Prescription',
    route: '/prescription',
    translationKey: 'global.menu.entities.prescription',
  },
];
