import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IVitalSign } from '../vital-sign.model';
import { VitalSignService } from '../service/vital-sign.service';

@Component({
  templateUrl: './vital-sign-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class VitalSignDeleteDialogComponent {
  vitalSign?: IVitalSign;

  protected vitalSignService = inject(VitalSignService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.vitalSignService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
