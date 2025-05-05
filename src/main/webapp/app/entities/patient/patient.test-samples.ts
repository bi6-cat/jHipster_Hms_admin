import { IPatient, NewPatient } from './patient.model';

export const sampleWithRequiredData: IPatient = {
  id: 2514,
  name: 'custody if who',
};

export const sampleWithPartialData: IPatient = {
  id: 6705,
  name: 'per',
  gender: 'toward sharply',
  dob: 'peninsula delicious or',
  phone: '1-576-589-6773 x98533',
  email: 'Alex.Hessel@hotmail.com',
};

export const sampleWithFullData: IPatient = {
  id: 9819,
  name: 'an',
  gender: 'outdo guilt',
  dob: 'across',
  address: 'worst',
  phone: '1-924-653-2422',
  email: 'Lorna.Hermiston@yahoo.com',
};

export const sampleWithNewData: NewPatient = {
  name: 'kick labourer brr',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
