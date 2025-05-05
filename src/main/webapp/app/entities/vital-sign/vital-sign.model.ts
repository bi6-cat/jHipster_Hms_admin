import dayjs from 'dayjs/esm';
import { IPatient } from 'app/entities/patient/patient.model';

export interface IVitalSign {
  id: number;
  measurementDate?: dayjs.Dayjs | null;
  bloodPressure?: string | null;
  heartRate?: number | null;
  respiratoryRate?: number | null;
  temperature?: number | null;
  oxygenSaturation?: number | null;
  bloodSugar?: number | null;
  patient?: Pick<IPatient, 'id' | 'name'> | null;
}

export type NewVitalSign = Omit<IVitalSign, 'id'> & { id: null };
