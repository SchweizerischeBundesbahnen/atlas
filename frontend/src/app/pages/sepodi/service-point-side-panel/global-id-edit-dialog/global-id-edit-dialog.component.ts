import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, of } from 'rxjs';
import { Country } from '../../../../api';
import { ServicePointInternalService } from '../../../../api/service/sepodi/service-point-internal.service';
import { GlobalIdValidator } from '../../../../core/validation/global-id/global-id-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { ValidationService } from '../../../../core/validation/validation.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';

export interface GlobalIdEditDialogData {
  servicePointNumber: number;
  country: Country;
  globalId?: string;
}

interface GlobalIdEditFormGroup {
  country: FormControl<Country>;
  globalId: FormControl<string | undefined>;
}

@Component({
  selector: 'atlas-global-id-edit-dialog',
  templateUrl: './global-id-edit-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    TextFieldComponent,
    ReactiveFormsModule,
    TranslatePipe,
  ],
})
export class GlobalIdEditDialogComponent {
  protected readonly data: GlobalIdEditDialogData = inject(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<GlobalIdEditDialogComponent>);
  private readonly servicePointService = inject(ServicePointInternalService);
  private readonly notificationService = inject(NotificationService);
  private readonly detailHelperService = inject(DetailDialogHelperService);

  readonly form = new FormGroup<GlobalIdEditFormGroup>({
    country: new FormControl(this.data.country, { nonNullable: true }),
    globalId: new FormControl(this.data.globalId, {
      nonNullable: true,
      validators: [
        Validators.required,
        GlobalIdValidator.countryPrefix,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        Validators.maxLength(128),
      ],
    }),
  });

  get canDelete(): boolean {
    return !!this.data.globalId;
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const globalId = this.form.controls.globalId.value;
      this.servicePointService
        .updateGlobalId(this.data.servicePointNumber, { globalId })
        .pipe(
          catchError(() => {
            this.dialogRef.close(false);
            return of();
          })
        )
        .subscribe(() => {
          this.notificationService.success('SEPODI.SERVICE_POINTS.GLOBAL_ID_EDIT.SUCCESS');
          this.dialogRef.close(true);
        });
    }
  }

  delete() {
    this.servicePointService
      .deleteGlobalId(this.data.servicePointNumber)
      .pipe(
        catchError(() => {
          this.dialogRef.close(false);
          return of();
        })
      )
      .subscribe(() => {
        this.notificationService.success('SEPODI.SERVICE_POINTS.GLOBAL_ID_EDIT.DELETE_SUCCESS');
        this.dialogRef.close(true);
      });
  }

  cancel() {
    this.detailHelperService.confirmLeaveDirtyForm(this.form).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(false);
      }
    });
  }
}
