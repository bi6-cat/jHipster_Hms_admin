export interface IPatient {
  id: number;
  name?: string | null;
  gender?: string | null;
  dob?: string | null;
  address?: string | null;
  phone?: string | null;
  email?: string | null;
}

export type NewPatient = Omit<IPatient, 'id'> & { id: null };
