import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IVitalSign } from '../vital-sign.model';

@Component({
  selector: 'jhi-vital-sign-detail',
  templateUrl: './vital-sign-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class VitalSignDetailComponent {
  vitalSign = input<IVitalSign | null>(null);

  previousState(): void {
    window.history.back();
  }
}
