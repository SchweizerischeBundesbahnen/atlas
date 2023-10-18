import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { TimetableHearingStatement, TimetableHearingStatementsService } from '../../../../api';
import { Subject } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StatementDetailFormGroup } from '../statement-detail-form-group';
import { takeUntil } from 'rxjs/operators';
import { ValidationService } from 'src/app/core/validation/validation.service';

@Component({
  selector: 'app-dialog',
  templateUrl: './statement.dialog.component.html',
})
export class StatementDialogComponent {
  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<StatementDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public form: FormGroup<StatementDetailFormGroup>,
    private readonly timetableHearingStatementsService: TimetableHearingStatementsService,
    private readonly notificationService: NotificationService,
  ) {}

  changeCantonAndAddComment() {
    const hearingStatement = this.form.value as TimetableHearingStatement;
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.updateStatement(this.form.value!.id!, hearingStatement);
      this.dialogRef.close(true);
    }
  }

  private updateStatement(id: number, statement: TimetableHearingStatement) {
    this.timetableHearingStatementsService
      .updateHearingStatement(id, statement)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(() => {
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS');
      });
  }

  goBackToStatementDetailEditMode() {
    this.dialogRef.close(false);
  }
}
