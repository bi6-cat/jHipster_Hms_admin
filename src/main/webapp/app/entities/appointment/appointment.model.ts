import dayjs from 'dayjs/esm';
import { IPatient } from 'app/entities/patient/patient.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';

export interface IAppointment {
  id: number;
  appointmentDate?: dayjs.Dayjs | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  reason?: string | null;
  status?: string | null;
  phone?: string | null;
  location?: string | null;
  appointmentType?: string | null;
  patient?: Pick<IPatient, 'id' | 'name'> | null;
  doctor?: Pick<IDoctor, 'id' | 'name'> | null;
}

export type NewAppointment = Omit<IAppointment, 'id'> & { id: null };
