import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IDisease } from '../disease.model';

@Component({
  selector: 'jhi-disease-detail',
  templateUrl: './disease-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class DiseaseDetailComponent {
  disease = input<IDisease | null>(null);

  previousState(): void {
    window.history.back();
  }
}
