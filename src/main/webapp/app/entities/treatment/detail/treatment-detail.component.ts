import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ITreatment } from '../treatment.model';

@Component({
  selector: 'jhi-treatment-detail',
  templateUrl: './treatment-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class TreatmentDetailComponent {
  treatment = input<ITreatment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
