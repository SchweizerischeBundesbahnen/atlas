import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {
  StopPointRestartWorkflowFormGroup,
  StopPointRestartWorkflowFormGroupBuilder
} from "./stop-point-restart-workflow-form-group";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ValidationService} from "../../../../core/validation/validation.service";
import {StopPointRestartWorkflow, StopPointWorkflowService, UserAdministrationService} from "../../../../api";
import {Router} from "@angular/router";
import {NotificationService} from "../../../../core/notification/notification.service";
import {Pages} from "../../../pages";
import {
  StopPointRejectWorkflowDialogData
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog-data";

@Component({
  selector: 'app-stop-point-restart-workflow-dialog',
  templateUrl: './stop-point-restart-workflow-dialog.component.html',
  styleUrls: ['./stop-point-restart-workflow-dialog.component.scss']
})
export class StopPointRestartWorkflowDialogComponent implements OnInit {

  formGroup!: FormGroup<StopPointRestartWorkflowFormGroup>;


  constructor(
    public dialogRef: MatDialogRef<StopPointRestartWorkflowDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StopPointRejectWorkflowDialogData,
    private detailHelperService: DetailHelperService,
    private router: Router,
    private notificationService: NotificationService,
    private stopPointWorkflowService: StopPointWorkflowService,
    private userAdministrationService: UserAdministrationService,
  ) {
  }

  ngOnInit(): void {
    this.formGroup = StopPointRestartWorkflowFormGroupBuilder.initFormGroup();
    this.populateUserDataFormFromAuthenticatedUser();
  }

  closeDialog() {
    this.detailHelperService.confirmLeaveDirtyForm(this.formGroup).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }

  private populateUserDataFormFromAuthenticatedUser() {
    this.formGroup.reset();
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.formGroup.controls.firstName.setValue(user.firstName);
      this.formGroup.controls.lastName.setValue(user.lastName);
    });
  }

  restartWorkflow() {
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      const stopPointRestartWorkflow =
        StopPointRestartWorkflowFormGroupBuilder.buildStopPointRestartWorkflow(this.formGroup);
      this.formGroup.disable();
      this.doRestart(stopPointRestartWorkflow)
    }
  }

   private doRestart(stopPointRestartWorkflow: StopPointRestartWorkflow) {
     this.stopPointWorkflowService.restartStopPointWorkflow(this.data.workflowId, stopPointRestartWorkflow)
       .subscribe((workflow) => {
         this.notificationService.success('WORKFLOW.NOTIFICATION.CHECK.RESTARTED');
         this.dialogRef.close();
         this.navigateToWorkflow(workflow.id!);
       })
   }

   private navigateToWorkflow(id: number) {
     this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
       this.router.navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, id]).then(() => {
       });
     })
   }
}
