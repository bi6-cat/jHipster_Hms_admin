import dayjs from 'dayjs/esm';

import { IHospitalFee, NewHospitalFee } from './hospital-fee.model';

export const sampleWithRequiredData: IHospitalFee = {
  id: 7535,
};

export const sampleWithPartialData: IHospitalFee = {
  id: 17613,
  serviceType: 'exalted strident yowza',
  description: 'sturdy ah honestly',
  feeDate: dayjs('2025-05-03'),
};

export const sampleWithFullData: IHospitalFee = {
  id: 16499,
  serviceType: 'back past',
  description: 'competent',
  amount: 9470.08,
  feeDate: dayjs('2025-05-03'),
  phone: '840.340.7013 x11511',
};

export const sampleWithNewData: NewHospitalFee = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
