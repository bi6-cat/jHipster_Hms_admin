import dayjs from 'dayjs/esm';

import { ITreatment, NewTreatment } from './treatment.model';

export const sampleWithRequiredData: ITreatment = {
  id: 10095,
};

export const sampleWithPartialData: ITreatment = {
  id: 4820,
  treatmentDescription: 'subtract including woot',
};

export const sampleWithFullData: ITreatment = {
  id: 23750,
  treatmentDescription: 'above pace scary',
  treatmentDate: dayjs('2025-05-03'),
};

export const sampleWithNewData: NewTreatment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
