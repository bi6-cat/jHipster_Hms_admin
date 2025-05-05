import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IVitalSign } from '../vital-sign.model';
import { VitalSignService } from '../service/vital-sign.service';
import { VitalSignFormGroup, VitalSignFormService } from './vital-sign-form.service';

@Component({
  selector: 'jhi-vital-sign-update',
  templateUrl: './vital-sign-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class VitalSignUpdateComponent implements OnInit {
  isSaving = false;
  vitalSign: IVitalSign | null = null;

  patientsSharedCollection: IPatient[] = [];

  protected vitalSignService = inject(VitalSignService);
  protected vitalSignFormService = inject(VitalSignFormService);
  protected patientService = inject(PatientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VitalSignFormGroup = this.vitalSignFormService.createVitalSignFormGroup();

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vitalSign }) => {
      this.vitalSign = vitalSign;
      if (vitalSign) {
        this.updateForm(vitalSign);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const vitalSign = this.vitalSignFormService.getVitalSign(this.editForm);
    if (vitalSign.id !== null) {
      this.subscribeToSaveResponse(this.vitalSignService.update(vitalSign));
    } else {
      this.subscribeToSaveResponse(this.vitalSignService.create(vitalSign));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVitalSign>>): void {
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

  protected updateForm(vitalSign: IVitalSign): void {
    this.vitalSign = vitalSign;
    this.vitalSignFormService.resetForm(this.editForm, vitalSign);

    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      vitalSign.patient,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.vitalSign?.patient)))
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));
  }
}
