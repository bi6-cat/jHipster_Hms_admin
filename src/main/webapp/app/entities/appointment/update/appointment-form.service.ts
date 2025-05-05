import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppointment, NewAppointment } from '../appointment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppointment for edit and NewAppointmentFormGroupInput for create.
 */
type AppointmentFormGroupInput = IAppointment | PartialWithRequiredKeyOf<NewAppointment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppointment | NewAppointment> = Omit<T, 'appointmentDate' | 'startTime' | 'endTime'> & {
  appointmentDate?: string | null;
  startTime?: string | null;
  endTime?: string | null;
};

type AppointmentFormRawValue = FormValueOf<IAppointment>;

type NewAppointmentFormRawValue = FormValueOf<NewAppointment>;

type AppointmentFormDefaults = Pick<NewAppointment, 'id' | 'appointmentDate' | 'startTime' | 'endTime'>;

type AppointmentFormGroupContent = {
  id: FormControl<AppointmentFormRawValue['id'] | NewAppointment['id']>;
  appointmentDate: FormControl<AppointmentFormRawValue['appointmentDate']>;
  startTime: FormControl<AppointmentFormRawValue['startTime']>;
  endTime: FormControl<AppointmentFormRawValue['endTime']>;
  reason: FormControl<AppointmentFormRawValue['reason']>;
  status: FormControl<AppointmentFormRawValue['status']>;
  phone: FormControl<AppointmentFormRawValue['phone']>;
  location: FormControl<AppointmentFormRawValue['location']>;
  appointmentType: FormControl<AppointmentFormRawValue['appointmentType']>;
  patient: FormControl<AppointmentFormRawValue['patient']>;
  doctor: FormControl<AppointmentFormRawValue['doctor']>;
};

export type AppointmentFormGroup = FormGroup<AppointmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppointmentFormService {
  createAppointmentFormGroup(appointment: AppointmentFormGroupInput = { id: null }): AppointmentFormGroup {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({
      ...this.getFormDefaults(),
      ...appointment,
    });
    return new FormGroup<AppointmentFormGroupContent>({
      id: new FormControl(
        { value: appointmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      appointmentDate: new FormControl(appointmentRawValue.appointmentDate),
      startTime: new FormControl(appointmentRawValue.startTime),
      endTime: new FormControl(appointmentRawValue.endTime),
      reason: new FormControl(appointmentRawValue.reason),
      status: new FormControl(appointmentRawValue.status),
      phone: new FormControl(appointmentRawValue.phone),
      location: new FormControl(appointmentRawValue.location),
      appointmentType: new FormControl(appointmentRawValue.appointmentType),
      patient: new FormControl(appointmentRawValue.patient),
      doctor: new FormControl(appointmentRawValue.doctor),
    });
  }

  getAppointment(form: AppointmentFormGroup): IAppointment | NewAppointment {
    return this.convertAppointmentRawValueToAppointment(form.getRawValue() as AppointmentFormRawValue | NewAppointmentFormRawValue);
  }

  resetForm(form: AppointmentFormGroup, appointment: AppointmentFormGroupInput): void {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({ ...this.getFormDefaults(), ...appointment });
    form.reset(
      {
        ...appointmentRawValue,
        id: { value: appointmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppointmentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      appointmentDate: currentTime,
      startTime: currentTime,
      endTime: currentTime,
    };
  }

  private convertAppointmentRawValueToAppointment(
    rawAppointment: AppointmentFormRawValue | NewAppointmentFormRawValue,
  ): IAppointment | NewAppointment {
    return {
      ...rawAppointment,
      appointmentDate: dayjs(rawAppointment.appointmentDate, DATE_TIME_FORMAT),
      startTime: dayjs(rawAppointment.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawAppointment.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertAppointmentToAppointmentRawValue(
    appointment: IAppointment | (Partial<NewAppointment> & AppointmentFormDefaults),
  ): AppointmentFormRawValue | PartialWithRequiredKeyOf<NewAppointmentFormRawValue> {
    return {
      ...appointment,
      appointmentDate: appointment.appointmentDate ? appointment.appointmentDate.format(DATE_TIME_FORMAT) : undefined,
      startTime: appointment.startTime ? appointment.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: appointment.endTime ? appointment.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
