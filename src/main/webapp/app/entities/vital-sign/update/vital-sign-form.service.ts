import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IVitalSign, NewVitalSign } from '../vital-sign.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVitalSign for edit and NewVitalSignFormGroupInput for create.
 */
type VitalSignFormGroupInput = IVitalSign | PartialWithRequiredKeyOf<NewVitalSign>;

type VitalSignFormDefaults = Pick<NewVitalSign, 'id'>;

type VitalSignFormGroupContent = {
  id: FormControl<IVitalSign['id'] | NewVitalSign['id']>;
  measurementDate: FormControl<IVitalSign['measurementDate']>;
  bloodPressure: FormControl<IVitalSign['bloodPressure']>;
  heartRate: FormControl<IVitalSign['heartRate']>;
  respiratoryRate: FormControl<IVitalSign['respiratoryRate']>;
  temperature: FormControl<IVitalSign['temperature']>;
  oxygenSaturation: FormControl<IVitalSign['oxygenSaturation']>;
  bloodSugar: FormControl<IVitalSign['bloodSugar']>;
  patient: FormControl<IVitalSign['patient']>;
};

export type VitalSignFormGroup = FormGroup<VitalSignFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VitalSignFormService {
  createVitalSignFormGroup(vitalSign: VitalSignFormGroupInput = { id: null }): VitalSignFormGroup {
    const vitalSignRawValue = {
      ...this.getFormDefaults(),
      ...vitalSign,
    };
    return new FormGroup<VitalSignFormGroupContent>({
      id: new FormControl(
        { value: vitalSignRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      measurementDate: new FormControl(vitalSignRawValue.measurementDate),
      bloodPressure: new FormControl(vitalSignRawValue.bloodPressure),
      heartRate: new FormControl(vitalSignRawValue.heartRate),
      respiratoryRate: new FormControl(vitalSignRawValue.respiratoryRate),
      temperature: new FormControl(vitalSignRawValue.temperature),
      oxygenSaturation: new FormControl(vitalSignRawValue.oxygenSaturation),
      bloodSugar: new FormControl(vitalSignRawValue.bloodSugar),
      patient: new FormControl(vitalSignRawValue.patient),
    });
  }

  getVitalSign(form: VitalSignFormGroup): IVitalSign | NewVitalSign {
    return form.getRawValue() as IVitalSign | NewVitalSign;
  }

  resetForm(form: VitalSignFormGroup, vitalSign: VitalSignFormGroupInput): void {
    const vitalSignRawValue = { ...this.getFormDefaults(), ...vitalSign };
    form.reset(
      {
        ...vitalSignRawValue,
        id: { value: vitalSignRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): VitalSignFormDefaults {
    return {
      id: null,
    };
  }
}
