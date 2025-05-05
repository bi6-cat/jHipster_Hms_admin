import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IDisease } from 'app/entities/disease/disease.model';
import { DiseaseService } from 'app/entities/disease/service/disease.service';
import { TreatmentService } from '../service/treatment.service';
import { ITreatment } from '../treatment.model';
import { TreatmentFormGroup, TreatmentFormService } from './treatment-form.service';

@Component({
  selector: 'jhi-treatment-update',
  templateUrl: './treatment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TreatmentUpdateComponent implements OnInit {
  isSaving = false;
  treatment: ITreatment | null = null;

  patientsSharedCollection: IPatient[] = [];
  doctorsSharedCollection: IDoctor[] = [];
  diseasesSharedCollection: IDisease[] = [];

  protected treatmentService = inject(TreatmentService);
  protected treatmentFormService = inject(TreatmentFormService);
  protected patientService = inject(PatientService);
  protected doctorService = inject(DoctorService);
  protected diseaseService = inject(DiseaseService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TreatmentFormGroup = this.treatmentFormService.createTreatmentFormGroup();

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  compareDoctor = (o1: IDoctor | null, o2: IDoctor | null): boolean => this.doctorService.compareDoctor(o1, o2);

  compareDisease = (o1: IDisease | null, o2: IDisease | null): boolean => this.diseaseService.compareDisease(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ treatment }) => {
      this.treatment = treatment;
      if (treatment) {
        this.updateForm(treatment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const treatment = this.treatmentFormService.getTreatment(this.editForm);
    if (treatment.id !== null) {
      this.subscribeToSaveResponse(this.treatmentService.update(treatment));
    } else {
      this.subscribeToSaveResponse(this.treatmentService.create(treatment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITreatment>>): void {
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

  protected updateForm(treatment: ITreatment): void {
    this.treatment = treatment;
    this.treatmentFormService.resetForm(this.editForm, treatment);

    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      treatment.patient,
    );
    this.doctorsSharedCollection = this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(
      this.doctorsSharedCollection,
      treatment.doctor,
    );
    this.diseasesSharedCollection = this.diseaseService.addDiseaseToCollectionIfMissing<IDisease>(
      this.diseasesSharedCollection,
      treatment.disease,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.treatment?.patient)))
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));

    this.doctorService
      .query()
      .pipe(map((res: HttpResponse<IDoctor[]>) => res.body ?? []))
      .pipe(map((doctors: IDoctor[]) => this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(doctors, this.treatment?.doctor)))
      .subscribe((doctors: IDoctor[]) => (this.doctorsSharedCollection = doctors));

    this.diseaseService
      .query()
      .pipe(map((res: HttpResponse<IDisease[]>) => res.body ?? []))
      .pipe(map((diseases: IDisease[]) => this.diseaseService.addDiseaseToCollectionIfMissing<IDisease>(diseases, this.treatment?.disease)))
      .subscribe((diseases: IDisease[]) => (this.diseasesSharedCollection = diseases));
  }
}
