import dayjs from 'dayjs/esm';

import { IVitalSign, NewVitalSign } from './vital-sign.model';

export const sampleWithRequiredData: IVitalSign = {
  id: 6352,
};

export const sampleWithPartialData: IVitalSign = {
  id: 19790,
  measurementDate: dayjs('2025-05-04'),
  bloodPressure: 'boom apud ditch',
  temperature: 10843.3,
  oxygenSaturation: 23539,
  bloodSugar: 30726,
};

export const sampleWithFullData: IVitalSign = {
  id: 29165,
  measurementDate: dayjs('2025-05-03'),
  bloodPressure: 'miskey youthfully',
  heartRate: 1402,
  respiratoryRate: 15194,
  temperature: 2625.88,
  oxygenSaturation: 23680,
  bloodSugar: 5288,
};

export const sampleWithNewData: NewVitalSign = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
