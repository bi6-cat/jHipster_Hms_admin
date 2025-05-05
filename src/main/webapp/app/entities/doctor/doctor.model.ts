export interface IDoctor {
  id: number;
  name?: string | null;
  lastName?: string | null;
  specialization?: string | null;
  phone?: string | null;
  email?: string | null;
}

export type NewDoctor = Omit<IDoctor, 'id'> & { id: null };
