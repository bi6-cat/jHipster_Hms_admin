import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPatient } from '../patient.model';

@Component({
  selector: 'jhi-patient-detail',
  templateUrl: './patient-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PatientDetailComponent {
  patient = input<IPatient | null>(null);

  previousState(): void {
    window.history.back();
  }
}
