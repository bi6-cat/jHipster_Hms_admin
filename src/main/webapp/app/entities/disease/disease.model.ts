import dayjs from 'dayjs/esm';
import { IPatient } from 'app/entities/patient/patient.model';

export interface IDisease {
  id: number;
  diseaseName?: string | null;
  diagnosisDate?: dayjs.Dayjs | null;
  patient?: Pick<IPatient, 'id' | 'name'> | null;
}

export type NewDisease = Omit<IDisease, 'id'> & { id: null };
