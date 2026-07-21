import { Component, inject, input, output } from '@angular/core';
import { DialogService } from '../../components/dialog/dialog.service';
import { AtlasButtonComponent } from '../../components/button/atlas-button.component';
import { ApplicationType } from '../../../api';
import { DialogData } from '../../components/dialog/dialog.data';

export interface Revokable {
  revoke: () => void;
}

@Component({
  selector: 'atlas-revoke-button',
  imports: [AtlasButtonComponent],
  templateUrl: './revoke-button.html',
})
export class RevokeButton {
  readonly revokeClicked = output<void>();
  readonly applicationType = input.required<ApplicationType>();
  readonly hidden = input(false);
  readonly disabled = input(false);
  private readonly dialogService = inject(DialogService);

  revoke() {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      } satisfies DialogData)
      .subscribe((confirmed) => {
        if (confirmed) {
          this.revokeClicked.emit();
        }
      });
  }
}
