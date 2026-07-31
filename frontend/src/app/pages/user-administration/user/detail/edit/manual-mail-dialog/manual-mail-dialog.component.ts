import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { DialogData } from '../../../../../../core/components/dialog/dialog.data';
import { DialogService } from '../../../../../../core/components/dialog/dialog.service';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasCharsetsValidator } from '../../../../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../../../../core/validation/field-lengths/atlas-field-length-validator';
import { User } from '../../../../../../api';
import { UserAdministrationService } from '../../../../../../api/service/user-administration/user-administration.service';
import { NotificationService } from '../../../../../../core/notification/notification.service';

export interface ManualMailDialogData extends DialogData {
  user: User;
}

@Component({
  selector: 'atlas-manual-mail-dialog',
  templateUrl: './manual-mail-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    MatDialogClose,
    ReactiveFormsModule,
    MatDialogActions,
    TranslatePipe,
    AtlasButtonComponent,
    TextFieldComponent,

  ],
  providers: [TranslatePipe],
})
export class ManualMailDialogComponent {
  private readonly dialogRef = inject<MatDialogRef<ManualMailDialogComponent, User | undefined>>(MatDialogRef);
  private readonly userAdministrationService = inject(UserAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogService = inject(DialogService);

  readonly data = inject<ManualMailDialogData>(MAT_DIALOG_DATA);

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];
  readonly azureMail = this.data.user.mail;
  readonly hasExistingOverride = !!this.data.user.manualMail && this.data.user.manualMail.trim().length > 0;

  form = new FormGroup({
    manualMail: new FormControl(this.data.user.manualMail ?? null, this.emailValidator),
  });

  confirm(): void {
    const manualMail = this.form.controls.manualMail.value?.trim();
    if (!manualMail) {
      this.performDelete();
      return;
    }

    this.form.disable();
    this.userAdministrationService.updateManualMail(this.data.user.sbbUserId, manualMail).subscribe({
      next: (user) => {
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.MANUAL_MAIL_SAVED');
        this.dialogRef.close(user);
      },
      error: () => this.form.enable(),
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
    this.form.disable();
    this.userAdministrationService.deleteManualMail(this.data.user.sbbUserId).subscribe({
      next: (user) => {
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.MANUAL_MAIL_DELETED');
        this.dialogRef.close(user);
      },
      error: () => this.form.enable(),
    });
  }
}
