import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {
  StopPointRestartWorkflowFormGroup,
  StopPointRestartWorkflowFormGroupBuilder
} from "./stop-point-restart-workflow-form-group";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ValidationService} from "../../../../core/validation/validation.service";
import {StopPointRestartWorkflow, StopPointWorkflowService} from "../../../../api";
import {Router} from "@angular/router";
import {NotificationService} from "../../../../core/notification/notification.service";
import {Pages} from "../../../pages";
import {
  StopPointRejectWorkflowDialogData
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog-data";

@Component({
  selector: 'app-stop-point-restart-workflow-dialog',
  templateUrl: './stop-point-restart-workflow-dialog.component.html'
})
export class StopPointRestartWorkflowDialogComponent implements OnInit {

  formGroup!: FormGroup<StopPointRestartWorkflowFormGroup>;


  constructor(
    public dialogRef: MatDialogRef<StopPointRestartWorkflowDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StopPointRejectWorkflowDialogData,
    private detailHelperService: DetailHelperService,
    private router: Router,
    private notificationService: NotificationService,
    private stopPointWorkflowService: StopPointWorkflowService

  ) {
  }

  ngOnInit(): void {
    this.formGroup = StopPointRestartWorkflowFormGroupBuilder.initFormGroup()
  }

  closeDialog() {
    this.detailHelperService.confirmLeaveDirtyForm(this.formGroup).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }

  restartWorkflow() {
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      const stopPointRestartWorkflow =
        StopPointRestartWorkflowFormGroupBuilder.buildStopPointRestartWorkflow(this.formGroup);
      this.formGroup.disable();
      this.doRestart(stopPointRestartWorkflow)
      console.log(stopPointRestartWorkflow);
    }
  }

   private doRestart(stopPointRestartWorkflow: StopPointRestartWorkflow) {
     this.stopPointWorkflowService.restartStopPointWorkflow(this.data.workflowId, stopPointRestartWorkflow)
       .subscribe(() => {
         this.notificationService.success('WORKFLOW.NOTIFICATION.CHECK.REJECTED');
         this.dialogRef.close();
         this.navigateToWorkflow();
       })
   }

   private navigateToWorkflow() {
     this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
       this.router.navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, this.data.workflowId]).then(() => {
       });
     })
   }
}
