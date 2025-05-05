import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IDisease } from '../disease.model';
import { DiseaseService } from '../service/disease.service';
import { DiseaseFormGroup, DiseaseFormService } from './disease-form.service';

@Component({
  selector: 'jhi-disease-update',
  templateUrl: './disease-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DiseaseUpdateComponent implements OnInit {
  isSaving = false;
  disease: IDisease | null = null;

  patientsSharedCollection: IPatient[] = [];

  protected diseaseService = inject(DiseaseService);
  protected diseaseFormService = inject(DiseaseFormService);
  protected patientService = inject(PatientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DiseaseFormGroup = this.diseaseFormService.createDiseaseFormGroup();

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ disease }) => {
      this.disease = disease;
      if (disease) {
        this.updateForm(disease);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const disease = this.diseaseFormService.getDisease(this.editForm);
    if (disease.id !== null) {
      this.subscribeToSaveResponse(this.diseaseService.update(disease));
    } else {
      this.subscribeToSaveResponse(this.diseaseService.create(disease));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDisease>>): void {
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

  protected updateForm(disease: IDisease): void {
    this.disease = disease;
    this.diseaseFormService.resetForm(this.editForm, disease);

    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      disease.patient,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.disease?.patient)))
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));
  }
}
