import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IHospitalFee, NewHospitalFee } from '../hospital-fee.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHospitalFee for edit and NewHospitalFeeFormGroupInput for create.
 */
type HospitalFeeFormGroupInput = IHospitalFee | PartialWithRequiredKeyOf<NewHospitalFee>;

type HospitalFeeFormDefaults = Pick<NewHospitalFee, 'id'>;

type HospitalFeeFormGroupContent = {
  id: FormControl<IHospitalFee['id'] | NewHospitalFee['id']>;
  serviceType: FormControl<IHospitalFee['serviceType']>;
  description: FormControl<IHospitalFee['description']>;
  amount: FormControl<IHospitalFee['amount']>;
  feeDate: FormControl<IHospitalFee['feeDate']>;
  phone: FormControl<IHospitalFee['phone']>;
  appointment: FormControl<IHospitalFee['appointment']>;
  patient: FormControl<IHospitalFee['patient']>;
};

export type HospitalFeeFormGroup = FormGroup<HospitalFeeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HospitalFeeFormService {
  createHospitalFeeFormGroup(hospitalFee: HospitalFeeFormGroupInput = { id: null }): HospitalFeeFormGroup {
    const hospitalFeeRawValue = {
      ...this.getFormDefaults(),
      ...hospitalFee,
    };
    return new FormGroup<HospitalFeeFormGroupContent>({
      id: new FormControl(
        { value: hospitalFeeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      serviceType: new FormControl(hospitalFeeRawValue.serviceType),
      description: new FormControl(hospitalFeeRawValue.description),
      amount: new FormControl(hospitalFeeRawValue.amount),
      feeDate: new FormControl(hospitalFeeRawValue.feeDate),
      phone: new FormControl(hospitalFeeRawValue.phone),
      appointment: new FormControl(hospitalFeeRawValue.appointment),
      patient: new FormControl(hospitalFeeRawValue.patient),
    });
  }

  getHospitalFee(form: HospitalFeeFormGroup): IHospitalFee | NewHospitalFee {
    return form.getRawValue() as IHospitalFee | NewHospitalFee;
  }

  resetForm(form: HospitalFeeFormGroup, hospitalFee: HospitalFeeFormGroupInput): void {
    const hospitalFeeRawValue = { ...this.getFormDefaults(), ...hospitalFee };
    form.reset(
      {
        ...hospitalFeeRawValue,
        id: { value: hospitalFeeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): HospitalFeeFormDefaults {
    return {
      id: null,
    };
  }
}
