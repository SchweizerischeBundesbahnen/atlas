import {Component, Inject, OnInit} from '@angular/core';
import {StopPointRejectWorkflowFormGroup, StopPointRejectWorkflowFormGroupBuilder} from "./stop-point-reject-workflow-form-group";
import {FormGroup} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {StopPointRejectWorkflowDialogData} from "./stop-point-reject-workflow-dialog-data";
import {NotificationService} from "../../../../core/notification/notification.service";
import {StopPointRejectWorkflow, StopPointWorkflowService, UserAdministrationService} from "../../../../api";
import {ValidationService} from "../../../../core/validation/validation.service";
import {Pages} from "../../../pages";
import {Router} from "@angular/router";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";

@Component({
  selector: 'app-stop-point-reject-workflow-dialog',
  templateUrl: './stop-point-reject-workflow-dialog.component.html',
  styleUrl: './stop-point-reject-workflow-dialog.component.scss'
})
export class StopPointRejectWorkflowDialogComponent implements OnInit {

  formGroup!: FormGroup<StopPointRejectWorkflowFormGroup>;
  email!: string;

  constructor(
    public dialogRef: MatDialogRef<StopPointRejectWorkflowDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StopPointRejectWorkflowDialogData,
    private readonly stopPointWorkflowService: StopPointWorkflowService,
    private userAdministrationService: UserAdministrationService,
    private notificationService: NotificationService,
    private detailHelperService: DetailHelperService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.formGroup = StopPointRejectWorkflowFormGroupBuilder.initFormGroup()
    this.populateUserDataFormFromAuthenticatedUser();
  }

  private populateUserDataFormFromAuthenticatedUser() {
    this.formGroup.reset();
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.formGroup.controls.firstName.setValue(user.firstName);
      this.formGroup.controls.lastName.setValue(user.lastName);
      this.email = user.mail!;
    });
  }

  closeDialog() {
    this.detailHelperService.confirmLeaveDirtyForm(this.formGroup).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }

  rejectWorkflow() {
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      const stopPointRejectWorkflow =
        StopPointRejectWorkflowFormGroupBuilder.buildStopPointRejectWorkflow(this.formGroup, this.email);
      this.formGroup.disable();
      if (this.data.rejectType === "REJECT") {
        this.doReject(stopPointRejectWorkflow);
      }
      if (this.data.rejectType === "CANCEL") {
        this.doCancel(stopPointRejectWorkflow);
      }
    }
  }

  private doCancel(stopPointRejectWorkflow: StopPointRejectWorkflow) {
    this.stopPointWorkflowService.cancelStopPointWorkflow(this.data.workflowId, stopPointRejectWorkflow)
      .subscribe(() => {
        this.notificationService.success('WORKFLOW.NOTIFICATION.CHECK.CANCELED');
        this.dialogRef.close();
        this.navigateToWorkflow();
      })
  }


  private doReject(stopPointRejectWorkflow: StopPointRejectWorkflow) {
    this.stopPointWorkflowService.rejectStopPointWorkflow(this.data.workflowId, stopPointRejectWorkflow)
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
