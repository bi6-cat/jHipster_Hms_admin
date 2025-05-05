import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPrescription, NewPrescription } from '../prescription.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPrescription for edit and NewPrescriptionFormGroupInput for create.
 */
type PrescriptionFormGroupInput = IPrescription | PartialWithRequiredKeyOf<NewPrescription>;

type PrescriptionFormDefaults = Pick<NewPrescription, 'id'>;

type PrescriptionFormGroupContent = {
  id: FormControl<IPrescription['id'] | NewPrescription['id']>;
  medicineName: FormControl<IPrescription['medicineName']>;
  form: FormControl<IPrescription['form']>;
  dosageMg: FormControl<IPrescription['dosageMg']>;
  instruction: FormControl<IPrescription['instruction']>;
  durationDays: FormControl<IPrescription['durationDays']>;
  note: FormControl<IPrescription['note']>;
  appointment: FormControl<IPrescription['appointment']>;
  doctor: FormControl<IPrescription['doctor']>;
  patient: FormControl<IPrescription['patient']>;
};

export type PrescriptionFormGroup = FormGroup<PrescriptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PrescriptionFormService {
  createPrescriptionFormGroup(prescription: PrescriptionFormGroupInput = { id: null }): PrescriptionFormGroup {
    const prescriptionRawValue = {
      ...this.getFormDefaults(),
      ...prescription,
    };
    return new FormGroup<PrescriptionFormGroupContent>({
      id: new FormControl(
        { value: prescriptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      medicineName: new FormControl(prescriptionRawValue.medicineName),
      form: new FormControl(prescriptionRawValue.form),
      dosageMg: new FormControl(prescriptionRawValue.dosageMg),
      instruction: new FormControl(prescriptionRawValue.instruction),
      durationDays: new FormControl(prescriptionRawValue.durationDays),
      note: new FormControl(prescriptionRawValue.note),
      appointment: new FormControl(prescriptionRawValue.appointment),
      doctor: new FormControl(prescriptionRawValue.doctor),
      patient: new FormControl(prescriptionRawValue.patient),
    });
  }

  getPrescription(form: PrescriptionFormGroup): IPrescription | NewPrescription {
    return form.getRawValue() as IPrescription | NewPrescription;
  }

  resetForm(form: PrescriptionFormGroup, prescription: PrescriptionFormGroupInput): void {
    const prescriptionRawValue = { ...this.getFormDefaults(), ...prescription };
    form.reset(
      {
        ...prescriptionRawValue,
        id: { value: prescriptionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PrescriptionFormDefaults {
    return {
      id: null,
    };
  }
}
