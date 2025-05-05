import { IPrescription, NewPrescription } from './prescription.model';

export const sampleWithRequiredData: IPrescription = {
  id: 21788,
};

export const sampleWithPartialData: IPrescription = {
  id: 7433,
  medicineName: 'ouch gadzooks describe',
  form: 'once',
  dosageMg: 869,
  instruction: 'amused',
  durationDays: 27617,
};

export const sampleWithFullData: IPrescription = {
  id: 30569,
  medicineName: 'cultivated before',
  form: 'stable feminize downshift',
  dosageMg: 29925,
  instruction: 'SUV loyally',
  durationDays: 14998,
  note: 'for willfully',
};

export const sampleWithNewData: NewPrescription = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
