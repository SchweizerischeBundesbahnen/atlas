import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from './model/status-change-data';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { NotificationService } from '../../../../core/notification/notification.service';
import { Subject, takeUntil } from 'rxjs';
import { TthChangeStatusFormGroup } from './model/tth-change-status-form-group';
import {
  TimetableHearingStatementInternalService
} from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import { ValidationService } from '../../../../core/validation/validation.service';

@Component({
  selector: 'atlas-tth-change-status-dialog',
  templateUrl: './tth-change-status-dialog.component.html',
  imports: [BaseChangeDialogComponent, ReactiveFormsModule],
})
export class TthChangeStatusDialogComponent {
  public readonly dialogRef = inject(MatDialogRef<TthChangeStatusDialogComponent, boolean>);
  private readonly data: StatusChangeData = inject(MAT_DIALOG_DATA);
  private readonly notificationService = inject(NotificationService);
  private readonly timetableHearingStatementsService = inject(TimetableHearingStatementInternalService);

  readonly formGroup = new FormGroup<TthChangeStatusFormGroup>({
    internalComment: new FormControl(this.data.internalComment, [AtlasFieldLengthValidator.statement]),
  });
  private readonly ngUnsubscribe = new Subject<void>();

  onClick(): void {
    let internalComment: string | undefined;
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      if (this.formGroup.controls['internalComment'].value) {
        internalComment = this.formGroup.controls['internalComment'].value;
      }
      this.timetableHearingStatementsService
        .updateHearingStatementStatus({
          ids: this.data.tths.map((value) => Number(value.id)),
          internalComment: internalComment,
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
