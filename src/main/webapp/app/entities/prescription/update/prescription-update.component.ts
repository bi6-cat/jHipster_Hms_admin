import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService } from 'app/entities/appointment/service/appointment.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { PrescriptionService } from '../service/prescription.service';
import { IPrescription } from '../prescription.model';
import { PrescriptionFormGroup, PrescriptionFormService } from './prescription-form.service';

@Component({
  selector: 'jhi-prescription-update',
  templateUrl: './prescription-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PrescriptionUpdateComponent implements OnInit {
  isSaving = false;
  prescription: IPrescription | null = null;

  appointmentsSharedCollection: IAppointment[] = [];
  doctorsSharedCollection: IDoctor[] = [];
  patientsSharedCollection: IPatient[] = [];

  protected prescriptionService = inject(PrescriptionService);
  protected prescriptionFormService = inject(PrescriptionFormService);
  protected appointmentService = inject(AppointmentService);
  protected doctorService = inject(DoctorService);
  protected patientService = inject(PatientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PrescriptionFormGroup = this.prescriptionFormService.createPrescriptionFormGroup();

  compareAppointment = (o1: IAppointment | null, o2: IAppointment | null): boolean => this.appointmentService.compareAppointment(o1, o2);

  compareDoctor = (o1: IDoctor | null, o2: IDoctor | null): boolean => this.doctorService.compareDoctor(o1, o2);

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prescription }) => {
      this.prescription = prescription;
      if (prescription) {
        this.updateForm(prescription);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const prescription = this.prescriptionFormService.getPrescription(this.editForm);
    if (prescription.id !== null) {
      this.subscribeToSaveResponse(this.prescriptionService.update(prescription));
    } else {
      this.subscribeToSaveResponse(this.prescriptionService.create(prescription));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPrescription>>): void {
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

  protected updateForm(prescription: IPrescription): void {
    this.prescription = prescription;
    this.prescriptionFormService.resetForm(this.editForm, prescription);

    this.appointmentsSharedCollection = this.appointmentService.addAppointmentToCollectionIfMissing<IAppointment>(
      this.appointmentsSharedCollection,
      prescription.appointment,
    );
    this.doctorsSharedCollection = this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(
      this.doctorsSharedCollection,
      prescription.doctor,
    );
    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      prescription.patient,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appointmentService
      .query()
      .pipe(map((res: HttpResponse<IAppointment[]>) => res.body ?? []))
      .pipe(
        map((appointments: IAppointment[]) =>
          this.appointmentService.addAppointmentToCollectionIfMissing<IAppointment>(appointments, this.prescription?.appointment),
        ),
      )
      .subscribe((appointments: IAppointment[]) => (this.appointmentsSharedCollection = appointments));

    this.doctorService
      .query()
      .pipe(map((res: HttpResponse<IDoctor[]>) => res.body ?? []))
      .pipe(map((doctors: IDoctor[]) => this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(doctors, this.prescription?.doctor)))
      .subscribe((doctors: IDoctor[]) => (this.doctorsSharedCollection = doctors));

    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(
        map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.prescription?.patient)),
      )
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));
  }
}
