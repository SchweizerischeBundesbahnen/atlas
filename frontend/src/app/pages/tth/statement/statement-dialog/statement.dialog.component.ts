import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TimetableHearingStatementV2 } from '../../../../api';
import { Subject } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StatementDetailFormGroup } from '../statement-detail/statement-detail-form-group';
import { takeUntil } from 'rxjs/operators';
import { ValidationService } from 'src/app/core/validation/validation.service';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-dialog',
  templateUrl: './statement.dialog.component.html',
  imports: [CommentComponent, ReactiveFormsModule, MatDialogActions, AtlasButtonComponent, TranslatePipe],
  providers: [TranslatePipe],
})
export class StatementDialogComponent {
  private ngUnsubscribe = new Subject<void>();

  public dialogRef = inject(MatDialogRef<StatementDialogComponent, boolean>);
  public form: FormGroup<StatementDetailFormGroup> = inject(MAT_DIALOG_DATA);
  private readonly timetableHearingStatementsService = inject(TimetableHearingStatementInternalService);
  private readonly notificationService = inject(NotificationService);

  changeCantonAndAddComment() {
    const hearingStatement = this.form.value as TimetableHearingStatementV2;
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.updateStatement(this.form.value!.id!, hearingStatement);
      this.dialogRef.close(true);
    }
  }

  private updateStatement(id: number, statement: TimetableHearingStatementV2) {
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
