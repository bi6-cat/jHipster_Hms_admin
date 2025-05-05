import dayjs from 'dayjs/esm';
import { IPatient } from 'app/entities/patient/patient.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { IDisease } from 'app/entities/disease/disease.model';

export interface ITreatment {
  id: number;
  treatmentDescription?: string | null;
  treatmentDate?: dayjs.Dayjs | null;
  patient?: Pick<IPatient, 'id' | 'name'> | null;
  doctor?: Pick<IDoctor, 'id' | 'name'> | null;
  disease?: Pick<IDisease, 'id' | 'diseaseName'> | null;
}

export type NewTreatment = Omit<ITreatment, 'id'> & { id: null };
