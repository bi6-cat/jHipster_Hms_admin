import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITreatment } from '../treatment.model';
import { TreatmentService } from '../service/treatment.service';

@Component({
  templateUrl: './treatment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TreatmentDeleteDialogComponent {
  treatment?: ITreatment;

  protected treatmentService = inject(TreatmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.treatmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
