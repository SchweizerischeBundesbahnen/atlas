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

export interface ManualMailOverrideDialogData extends DialogData {
  user: User;
}

@Component({
  selector: 'atlas-manual-mail-override-dialog',
  templateUrl: './manual-mail-override-dialog.component.html',
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
export class ManualMailOverrideDialogComponent {
  private readonly dialogRef = inject<MatDialogRef<ManualMailOverrideDialogComponent, User | undefined>>(MatDialogRef);
  private readonly userAdministrationService = inject(UserAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogService = inject(DialogService);

  readonly data = inject<ManualMailOverrideDialogData>(MAT_DIALOG_DATA);

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];
  readonly originalMail = this.data.user.originalMail;
  readonly hasExistingOverride = this.data.user.mail !== this.data.user.originalMail;

  form = new FormGroup({
    manualMailOverride: new FormControl(this.hasExistingOverride ? this.data.user.mail : null, this.emailValidator),
  });

  confirm(): void {
    const manualMailOverride = this.form.controls.manualMailOverride.value?.trim();
    if (!manualMailOverride) {
      this.performDelete();
      return;
    }

    this.form.disable();
    this.userAdministrationService.updateManualMail(this.data.user.sbbUserId, manualMailOverride).subscribe({
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
