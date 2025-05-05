import { IAppointment } from 'app/entities/appointment/appointment.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { IPatient } from 'app/entities/patient/patient.model';

export interface IPrescription {
  id: number;
  medicineName?: string | null;
  form?: string | null;
  dosageMg?: number | null;
  instruction?: string | null;
  durationDays?: number | null;
  note?: string | null;
  appointment?: Pick<IAppointment, 'id'> | null;
  doctor?: Pick<IDoctor, 'id' | 'name'> | null;
  patient?: Pick<IPatient, 'id' | 'name'> | null;
}

export type NewPrescription = Omit<IPrescription, 'id'> & { id: null };
