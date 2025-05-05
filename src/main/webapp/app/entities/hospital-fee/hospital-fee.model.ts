import dayjs from 'dayjs/esm';
import { IAppointment } from 'app/entities/appointment/appointment.model';
import { IPatient } from 'app/entities/patient/patient.model';

export interface IHospitalFee {
  id: number;
  serviceType?: string | null;
  description?: string | null;
  amount?: number | null;
  feeDate?: dayjs.Dayjs | null;
  phone?: string | null;
  appointment?: Pick<IAppointment, 'id'> | null;
  patient?: Pick<IPatient, 'id' | 'name'> | null;
}

export type NewHospitalFee = Omit<IHospitalFee, 'id'> & { id: null };
