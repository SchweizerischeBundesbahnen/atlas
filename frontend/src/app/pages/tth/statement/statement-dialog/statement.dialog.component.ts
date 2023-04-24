import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { TimetableHearingService } from '../../../../api';
import { Subject } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StatementDetailFormGroup } from '../statement-detail-form-group';

@Component({
  selector: 'app-dialog',
  templateUrl: './statement.dialog.component.html',
})
export class StatementDialogComponent {
  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<StatementDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FormGroup<StatementDetailFormGroup>,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly notificationService: NotificationService
  ) {}

  addCommentToStatement() {
    this.dialogRef.close();

    // if (this.tthStatementCommentFormGroup.valid) {
    //   if (this.tthStatementCommentFormGroup.controls['comment'].value) {
    //     this.data.ths.comment = this .tthStatementCommentFormGroup.controls['comment'].value;
    //   }
    //   this.timetableHearingService.updateHearingStatement(this.data.ths.id!, this.data.ths)
    //     .pipe(takeUntil(this.ngUnsubscribe))
    //     .subscribe(() => {
    //       this.notificationService.success('TTH.NOTIFICATION.STATUS_CHANGE.SUCCESS');
    //       this.dialogRef.close();
    //     })
    // }
  }
}
