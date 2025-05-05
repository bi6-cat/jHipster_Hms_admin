import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDisease, NewDisease } from '../disease.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDisease for edit and NewDiseaseFormGroupInput for create.
 */
type DiseaseFormGroupInput = IDisease | PartialWithRequiredKeyOf<NewDisease>;

type DiseaseFormDefaults = Pick<NewDisease, 'id'>;

type DiseaseFormGroupContent = {
  id: FormControl<IDisease['id'] | NewDisease['id']>;
  diseaseName: FormControl<IDisease['diseaseName']>;
  diagnosisDate: FormControl<IDisease['diagnosisDate']>;
  patient: FormControl<IDisease['patient']>;
};

export type DiseaseFormGroup = FormGroup<DiseaseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DiseaseFormService {
  createDiseaseFormGroup(disease: DiseaseFormGroupInput = { id: null }): DiseaseFormGroup {
    const diseaseRawValue = {
      ...this.getFormDefaults(),
      ...disease,
    };
    return new FormGroup<DiseaseFormGroupContent>({
      id: new FormControl(
        { value: diseaseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      diseaseName: new FormControl(diseaseRawValue.diseaseName),
      diagnosisDate: new FormControl(diseaseRawValue.diagnosisDate),
      patient: new FormControl(diseaseRawValue.patient),
    });
  }

  getDisease(form: DiseaseFormGroup): IDisease | NewDisease {
    return form.getRawValue() as IDisease | NewDisease;
  }

  resetForm(form: DiseaseFormGroup, disease: DiseaseFormGroupInput): void {
    const diseaseRawValue = { ...this.getFormDefaults(), ...disease };
    form.reset(
      {
        ...diseaseRawValue,
        id: { value: diseaseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DiseaseFormDefaults {
    return {
      id: null,
    };
  }
}
