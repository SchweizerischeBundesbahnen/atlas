import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from './model/status-change-data';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { NotificationService } from '../../../../core/notification/notification.service';
import { Subject, takeUntil } from 'rxjs';
import { TthChangeStatusFormGroup } from './model/tth-change-status-form-group';
import { ValidationService } from 'src/app/core/validation/validation.service';
import { TimetableHearingStatementsService } from '../../../../api';

@Component({
  selector: 'app-tth-change-status-dialog',
  templateUrl: './tth-change-status-dialog.component.html',
})
export class TthChangeStatusDialogComponent {
  formGroup = new FormGroup<TthChangeStatusFormGroup>({
    justification: new FormControl(this.data.justification, [AtlasFieldLengthValidator.statement]),
  });
  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<TthChangeStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StatusChangeData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingStatementsService: TimetableHearingStatementsService,
  ) {}

  onClick(): void {
    let justification: string | undefined;
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      if (this.formGroup.controls['justification'].value) {
        justification = this.formGroup.controls['justification'].value;
      }
      this.timetableHearingStatementsService
        .updateHearingStatementStatus({
          ids: this.data.tths.map((value) => Number(value.id)),
          justification: justification,
          statementStatus: this.data.statementStatus,
        })
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.notificationService.success('TTH.NOTIFICATION.STATUS_CHANGE.SUCCESS');
          this.dialogRef.close(true);
        });
    }
  }
}
