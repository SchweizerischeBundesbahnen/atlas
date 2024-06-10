import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Client, ReadServicePointVersion, StopPointAddWorkflow, StopPointWorkflowService} from "../../../../api";
import {AddStopPointWorkflowDialogData} from "./add-stop-point-workflow-dialog-data";
import {FormGroup} from "@angular/forms";
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-form/stop-point-workflow-detail-form-group";
import {ValidationService} from "../../../../core/validation/validation.service";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {NotificationService} from "../../../../core/notification/notification.service";
import {Router} from "@angular/router";
import {Pages} from "../../../pages";

@Component({
  selector: 'app-workflow-dialog',
  templateUrl: './add-stop-point-workflow.component.html',
})
export class AddStopPointWorkflowComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddStopPointWorkflowComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddStopPointWorkflowDialogData,
    private detailHelperService: DetailHelperService,
    private stopPointWorkflowService: StopPointWorkflowService,
    private notificationService: NotificationService,
    private router: Router,
  ) {
  }

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  ngOnInit() {
    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup();
  }

  addWorkflow() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const workflow = this.stopPointToWorkflowInfo(this.data.stopPoint);
      workflow.workflowComment = this.form.controls.workflowComment.value!;
      workflow.ccEmails = this.form.controls.ccEmails.value!;
      workflow.examinants = this.form.controls.examinants.value.map(examinant => examinant as Client);

      this.form.disable();
      this.stopPointWorkflowService.addStopPointWorkflow(workflow).subscribe(createdWorkflow => {
        this.notificationService.success('WORKFLOW.NOTIFICATION.START.SUCCESS');
        this.dialogRef.close();
        this.router.navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, createdWorkflow.id]).then();
      })
    }
  }

  private stopPointToWorkflowInfo(stopPoint: ReadServicePointVersion): StopPointAddWorkflow {
    return {
      versionId: stopPoint.id!,
      sloid: stopPoint.sloid!,
    }
  }

  cancel() {
    this.detailHelperService.confirmLeaveDirtyForm(this.form).subscribe(confirmed => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }
}
