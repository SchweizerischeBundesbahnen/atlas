import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StopPointAddWorkflow, StopPointPerson } from '../../../../api';
import { AddStopPointWorkflowDialogData } from './add-stop-point-workflow-dialog-data';
import { FormGroup } from '@angular/forms';
import { ValidationService } from '../../../../core/validation/validation.service';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';
import { UserService } from '../../../../core/auth/user/user.service';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from '../detail-page/detail-form/stop-point-workflow-detail-form-group';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { StopPointWorkflowDetailFormComponent } from '../detail-page/detail-form/stop-point-workflow-detail-form.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';

@Component({
  selector: 'atlas-workflow-dialog',
  templateUrl: './add-stop-point-workflow.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./add-stop-point-workflow.component.scss'],
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    StopPointWorkflowDetailFormComponent,
    DialogFooterComponent,
    TranslatePipe,
  ],
})
export class AddStopPointWorkflowComponent implements OnInit {
  public readonly data: AddStopPointWorkflowDialogData = inject(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<AddStopPointWorkflowComponent>);
  private readonly detailHelperService = inject(DetailDialogHelperService);
  private readonly stopPointWorkflowService = inject(StopPointWorkflowService);
  private readonly notificationService = inject(NotificationService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  ngOnInit() {
    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup();
    this.form.controls.designationOfficial.setValue(this.data.stopPoint.designationOfficial);
  }

  addWorkflow() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const workflow: StopPointAddWorkflow = {
        applicantMail: this.userService.currentUser!.email,
        versionId: this.data.stopPoint.id!,
        sloid: this.data.stopPoint.sloid!,
        workflowComment: this.form.controls.workflowComment.value!,
        ccEmails: this.form.controls.ccEmails.value!,
        examinants: this.form.controls.examinants.controls
          .filter((control) => !control.disabled)
          .map((examinant) => {
            if (!examinant.value.firstName) {
              examinant.controls.firstName.setValue(null);
            }
            if (!examinant.value.lastName) {
              examinant.controls.lastName.setValue(null);
            }
            return examinant.value as StopPointPerson;
          }),
      };
      this.form.disable();
      this.stopPointWorkflowService.addStopPointWorkflow(workflow).subscribe((createdWorkflow) => {
        this.notificationService.success('WORKFLOW.NOTIFICATION.ADD.SUCCESS');
        this.dialogRef.close();
        this.router.navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, createdWorkflow.id]).then();
      });
    }
  }

  cancel() {
    this.detailHelperService.confirmLeaveDirtyForm(this.form).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }
}
