import dayjs from 'dayjs/esm';

import { IDisease, NewDisease } from './disease.model';

export const sampleWithRequiredData: IDisease = {
  id: 8371,
};

export const sampleWithPartialData: IDisease = {
  id: 25415,
};

export const sampleWithFullData: IDisease = {
  id: 12461,
  diseaseName: 'slide',
  diagnosisDate: dayjs('2025-05-04'),
};

export const sampleWithNewData: NewDisease = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
