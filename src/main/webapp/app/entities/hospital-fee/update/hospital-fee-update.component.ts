import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService } from 'app/entities/appointment/service/appointment.service';
import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { HospitalFeeService } from '../service/hospital-fee.service';
import { IHospitalFee } from '../hospital-fee.model';
import { HospitalFeeFormGroup, HospitalFeeFormService } from './hospital-fee-form.service';

@Component({
  selector: 'jhi-hospital-fee-update',
  templateUrl: './hospital-fee-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HospitalFeeUpdateComponent implements OnInit {
  isSaving = false;
  hospitalFee: IHospitalFee | null = null;

  appointmentsSharedCollection: IAppointment[] = [];
  patientsSharedCollection: IPatient[] = [];

  protected hospitalFeeService = inject(HospitalFeeService);
  protected hospitalFeeFormService = inject(HospitalFeeFormService);
  protected appointmentService = inject(AppointmentService);
  protected patientService = inject(PatientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: HospitalFeeFormGroup = this.hospitalFeeFormService.createHospitalFeeFormGroup();

  compareAppointment = (o1: IAppointment | null, o2: IAppointment | null): boolean => this.appointmentService.compareAppointment(o1, o2);

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hospitalFee }) => {
      this.hospitalFee = hospitalFee;
      if (hospitalFee) {
        this.updateForm(hospitalFee);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const hospitalFee = this.hospitalFeeFormService.getHospitalFee(this.editForm);
    if (hospitalFee.id !== null) {
      this.subscribeToSaveResponse(this.hospitalFeeService.update(hospitalFee));
    } else {
      this.subscribeToSaveResponse(this.hospitalFeeService.create(hospitalFee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHospitalFee>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(hospitalFee: IHospitalFee): void {
    this.hospitalFee = hospitalFee;
    this.hospitalFeeFormService.resetForm(this.editForm, hospitalFee);

    this.appointmentsSharedCollection = this.appointmentService.addAppointmentToCollectionIfMissing<IAppointment>(
      this.appointmentsSharedCollection,
      hospitalFee.appointment,
    );
    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      hospitalFee.patient,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appointmentService
      .query()
      .pipe(map((res: HttpResponse<IAppointment[]>) => res.body ?? []))
      .pipe(
        map((appointments: IAppointment[]) =>
          this.appointmentService.addAppointmentToCollectionIfMissing<IAppointment>(appointments, this.hospitalFee?.appointment),
        ),
      )
      .subscribe((appointments: IAppointment[]) => (this.appointmentsSharedCollection = appointments));

    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(
        map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.hospitalFee?.patient)),
      )
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));
  }
}
