import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPrescription } from '../prescription.model';

@Component({
  selector: 'jhi-prescription-detail',
  templateUrl: './prescription-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PrescriptionDetailComponent {
  prescription = input<IPrescription | null>(null);

  previousState(): void {
    window.history.back();
  }
}
