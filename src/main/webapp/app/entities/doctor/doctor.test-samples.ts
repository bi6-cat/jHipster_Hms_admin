import { IDoctor, NewDoctor } from './doctor.model';

export const sampleWithRequiredData: IDoctor = {
  id: 670,
  name: 'toothbrush righteously boo',
};

export const sampleWithPartialData: IDoctor = {
  id: 22203,
  name: 'actually',
  lastName: 'Conroy',
  email: 'Wendy_Volkman22@yahoo.com',
};

export const sampleWithFullData: IDoctor = {
  id: 20131,
  name: 'wildly ick',
  lastName: 'Strosin',
  specialization: 'gosh',
  phone: '980-652-4150 x136',
  email: 'Bonita.Dickinson97@gmail.com',
};

export const sampleWithNewData: NewDoctor = {
  name: 'yum',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
