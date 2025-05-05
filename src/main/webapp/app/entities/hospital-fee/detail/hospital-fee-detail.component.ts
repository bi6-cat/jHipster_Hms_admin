import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IHospitalFee } from '../hospital-fee.model';

@Component({
  selector: 'jhi-hospital-fee-detail',
  templateUrl: './hospital-fee-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class HospitalFeeDetailComponent {
  hospitalFee = input<IHospitalFee | null>(null);

  previousState(): void {
    window.history.back();
  }
}
