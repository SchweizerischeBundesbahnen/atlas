import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { disabled, form } from '@angular/forms/signals';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { DialogData } from '../../../../../../core/components/dialog/dialog.data';
import { DialogService } from '../../../../../../core/components/dialog/dialog.service';
import { AtlasTextFieldComponent } from '@atlas/form';
import { FormValidators } from '../../../../../../core/validation/form-validators.service';
import { User } from '../../../../../../api';
import { UserAdministrationService } from '../../../../../../api/service/user-administration/user-administration.service';
import { NotificationService } from '../../../../../../core/notification/notification.service';

export interface ManualMailOverrideDialogData extends DialogData {
  user: User;
}

@Component({
  selector: 'atlas-manual-mail-override-dialog',
  templateUrl: './manual-mail-override-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatDialogClose, MatDialogActions, TranslatePipe, AtlasButtonComponent, AtlasTextFieldComponent],
  providers: [TranslatePipe],
})
export class ManualMailOverrideDialogComponent {
  private readonly dialogRef = inject<MatDialogRef<ManualMailOverrideDialogComponent, User | undefined>>(MatDialogRef);
  private readonly userAdministrationService = inject(UserAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogService = inject(DialogService);
  private readonly formValidators = inject(FormValidators);

  readonly data = inject<ManualMailOverrideDialogData>(MAT_DIALOG_DATA);

  readonly originalMail = this.data.user.originalMail;
  readonly hasExistingOverride = this.data.user.mail !== this.data.user.originalMail;

  readonly pending = signal(false);
  readonly formModel = signal({ manualMailOverride: this.hasExistingOverride ? (this.data.user.mail ?? '') : '' });
  readonly form = form(this.formModel, (path) => {
    disabled(path, { when: () => this.pending() });
    this.formValidators.email(path.manualMailOverride);
    this.formValidators.maxLength(path.manualMailOverride, 100);
  });

  confirm(): void {
    this.form().markAsTouched();
    if (!this.form().valid() || this.pending()) {
      return;
    }
    const manualMailOverride = this.formModel().manualMailOverride.trim();
    if (!manualMailOverride) {
      this.performDelete();
      return;
    }

    this.pending.set(true);
    this.userAdministrationService.updateManualMail(this.data.user.sbbUserId, manualMailOverride).subscribe({
      next: (user) => {
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.MANUAL_MAIL_SAVED');
        this.dialogRef.close(user);
      },
      error: (error) => {
        this.pending.set(false);
        this.notificationService.error(error);
      },
    });
  }

  delete(): void {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
        message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.DELETE_CONFIRM',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.performDelete();
        }
      });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  private performDelete(): void {
    if (this.pending()) {
      return;
    }
    this.pending.set(true);
    this.userAdministrationService.deleteManualMail(this.data.user.sbbUserId).subscribe({
      next: (user) => {
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.MANUAL_MAIL_DELETED');
        this.dialogRef.close(user);
      },
      error: (error) => {
        this.pending.set(false);
        this.notificationService.error(error);
      },
    });
  }
}
