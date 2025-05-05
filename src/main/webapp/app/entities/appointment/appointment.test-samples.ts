import dayjs from 'dayjs/esm';

import { IAppointment, NewAppointment } from './appointment.model';

export const sampleWithRequiredData: IAppointment = {
  id: 13399,
};

export const sampleWithPartialData: IAppointment = {
  id: 9862,
  endTime: dayjs('2025-05-03T19:01'),
  status: 'athwart',
  location: 'gracefully instead',
};

export const sampleWithFullData: IAppointment = {
  id: 5963,
  appointmentDate: dayjs('2025-05-03T18:31'),
  startTime: dayjs('2025-05-04T11:03'),
  endTime: dayjs('2025-05-04T02:28'),
  reason: 'jaggedly apricot bah',
  status: 'airport why incomparable',
  phone: '1-687-605-6935 x840',
  location: 'fooey foretell',
  appointmentType: 'ridge forenenst',
};

export const sampleWithNewData: NewAppointment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
