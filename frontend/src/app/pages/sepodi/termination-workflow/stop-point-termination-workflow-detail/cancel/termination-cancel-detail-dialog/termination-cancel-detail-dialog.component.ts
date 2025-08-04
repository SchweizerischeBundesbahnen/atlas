import { Component, inject, OnInit } from '@angular/core';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { TerminationCancelFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { TerminationCancelDetailDialogData } from '../termination-cancel-dialog.service';
import { ValidationService } from '../../../../../../core/validation/validation.service';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import { NotificationService } from '../../../../../../core/notification/notification.service';

@Component({
  selector: 'app-termination-cancel-detail-dialog',
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    TranslatePipe,
    CommentComponent,
  ],
  templateUrl: './termination-cancel-detail-dialog.component.html',
})
export class TerminationCancelDetailDialogComponent implements OnInit {
  private readonly dialogRef = inject(
    MatDialogRef<TerminationCancelDetailDialogComponent>
  );
  private readonly decisionDetailDialogData: TerminationCancelDetailDialogData =
    inject(MAT_DIALOG_DATA);

  private readonly workflowService = inject(WorkflowService);
  private readonly notificationService = inject(NotificationService);

  form!: FormGroup<TerminationCancelFormGroup>;

  ngOnInit(): void {
    this.form = this.decisionDetailDialogData.cancelComment;
  }

  close(result?: boolean) {
    this.dialogRef.close(result);
  }

  cancelTermination() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const cancelComment = this.form.controls.cancelComment.value;
      const workflowId = this.decisionDetailDialogData.workflowId;
      this.workflowService
        .cancelTermination(workflowId, {
          cancelComment: cancelComment!,
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
