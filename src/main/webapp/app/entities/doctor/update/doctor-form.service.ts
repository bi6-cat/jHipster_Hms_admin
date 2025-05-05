import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDoctor, NewDoctor } from '../doctor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDoctor for edit and NewDoctorFormGroupInput for create.
 */
type DoctorFormGroupInput = IDoctor | PartialWithRequiredKeyOf<NewDoctor>;

type DoctorFormDefaults = Pick<NewDoctor, 'id'>;

type DoctorFormGroupContent = {
  id: FormControl<IDoctor['id'] | NewDoctor['id']>;
  name: FormControl<IDoctor['name']>;
  lastName: FormControl<IDoctor['lastName']>;
  specialization: FormControl<IDoctor['specialization']>;
  phone: FormControl<IDoctor['phone']>;
  email: FormControl<IDoctor['email']>;
};

export type DoctorFormGroup = FormGroup<DoctorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DoctorFormService {
  createDoctorFormGroup(doctor: DoctorFormGroupInput = { id: null }): DoctorFormGroup {
    const doctorRawValue = {
      ...this.getFormDefaults(),
      ...doctor,
    };
    return new FormGroup<DoctorFormGroupContent>({
      id: new FormControl(
        { value: doctorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(doctorRawValue.name, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(doctorRawValue.lastName),
      specialization: new FormControl(doctorRawValue.specialization),
      phone: new FormControl(doctorRawValue.phone),
      email: new FormControl(doctorRawValue.email),
    });
  }

  getDoctor(form: DoctorFormGroup): IDoctor | NewDoctor {
    return form.getRawValue() as IDoctor | NewDoctor;
  }

  resetForm(form: DoctorFormGroup, doctor: DoctorFormGroupInput): void {
    const doctorRawValue = { ...this.getFormDefaults(), ...doctor };
    form.reset(
      {
        ...doctorRawValue,
        id: { value: doctorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DoctorFormDefaults {
    return {
      id: null,
    };
  }
}
