import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { TerminationAbortFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import { TerminationAbortDetailDialogData } from '../termination-abort-dialog.service';
import { ValidationService } from '../../../../../../core/validation/validation.service';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { NotificationService } from '../../../../../../core/notification/notification.service';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-termination-abort-detail-dialog',
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    CommentComponent,
    DialogFooterComponent,
    TranslatePipe,
  ],
  templateUrl: './termination-abort-detail-dialog.component.html',
})
export class TerminationAbortDetailDialogComponent implements OnInit {
  private readonly dialogRef = inject(
    MatDialogRef<TerminationAbortDetailDialogComponent>
  );
  private readonly decisionDetailDialogData: TerminationAbortDetailDialogData =
    inject(MAT_DIALOG_DATA);

  private readonly workflowService = inject(
    StopPointTerminationWorkflowService
  );
  private readonly notificationService = inject(NotificationService);

  form!: FormGroup<TerminationAbortFormGroup>;

  ngOnInit(): void {
    this.form = this.decisionDetailDialogData.abortComment;
  }

  close(result?: boolean) {
    this.dialogRef.close(result);
  }

  abortTermination() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const abortComment = this.form.controls.abortComment.value;
      const workflowId = this.decisionDetailDialogData.workflowId;
      this.workflowService
        .abortTermination(workflowId, {
          abortComment: abortComment!,
        })
        .subscribe(() => {
          this.notificationService.success(
            'TERMINATION_WORKFLOW.NOTIFICATION.CANCEL.SUCCESS'
          );
          this.close(true);
        });
    }
  }
}
