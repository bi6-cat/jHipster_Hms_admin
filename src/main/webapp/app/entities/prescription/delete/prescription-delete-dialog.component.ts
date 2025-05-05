import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPrescription } from '../prescription.model';
import { PrescriptionService } from '../service/prescription.service';

@Component({
  templateUrl: './prescription-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PrescriptionDeleteDialogComponent {
  prescription?: IPrescription;

  protected prescriptionService = inject(PrescriptionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.prescriptionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
