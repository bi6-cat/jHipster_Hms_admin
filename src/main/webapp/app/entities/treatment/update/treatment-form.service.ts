import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITreatment, NewTreatment } from '../treatment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITreatment for edit and NewTreatmentFormGroupInput for create.
 */
type TreatmentFormGroupInput = ITreatment | PartialWithRequiredKeyOf<NewTreatment>;

type TreatmentFormDefaults = Pick<NewTreatment, 'id'>;

type TreatmentFormGroupContent = {
  id: FormControl<ITreatment['id'] | NewTreatment['id']>;
  treatmentDescription: FormControl<ITreatment['treatmentDescription']>;
  treatmentDate: FormControl<ITreatment['treatmentDate']>;
  patient: FormControl<ITreatment['patient']>;
  doctor: FormControl<ITreatment['doctor']>;
  disease: FormControl<ITreatment['disease']>;
};

export type TreatmentFormGroup = FormGroup<TreatmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TreatmentFormService {
  createTreatmentFormGroup(treatment: TreatmentFormGroupInput = { id: null }): TreatmentFormGroup {
    const treatmentRawValue = {
      ...this.getFormDefaults(),
      ...treatment,
    };
    return new FormGroup<TreatmentFormGroupContent>({
      id: new FormControl(
        { value: treatmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      treatmentDescription: new FormControl(treatmentRawValue.treatmentDescription),
      treatmentDate: new FormControl(treatmentRawValue.treatmentDate),
      patient: new FormControl(treatmentRawValue.patient),
      doctor: new FormControl(treatmentRawValue.doctor),
      disease: new FormControl(treatmentRawValue.disease),
    });
  }

  getTreatment(form: TreatmentFormGroup): ITreatment | NewTreatment {
    return form.getRawValue() as ITreatment | NewTreatment;
  }

  resetForm(form: TreatmentFormGroup, treatment: TreatmentFormGroupInput): void {
    const treatmentRawValue = { ...this.getFormDefaults(), ...treatment };
    form.reset(
      {
        ...treatmentRawValue,
        id: { value: treatmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TreatmentFormDefaults {
    return {
      id: null,
    };
  }
}
