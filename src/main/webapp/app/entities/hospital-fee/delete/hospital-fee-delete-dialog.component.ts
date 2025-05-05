import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IHospitalFee } from '../hospital-fee.model';
import { HospitalFeeService } from '../service/hospital-fee.service';

@Component({
  templateUrl: './hospital-fee-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HospitalFeeDeleteDialogComponent {
  hospitalFee?: IHospitalFee;

  protected hospitalFeeService = inject(HospitalFeeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.hospitalFeeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
