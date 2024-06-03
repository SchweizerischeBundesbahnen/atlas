import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NotificationService} from "../../../../core/notification/notification.service";
import {
  ReadServicePointVersion,
  StopPointAddWorkflow,
  StopPointWorkflowService,
  UserAdministrationService
} from "../../../../api";
import {AddStopPointWorkflowDialogData} from "./add-stop-point-workflow-dialog-data";
import {FormGroup} from "@angular/forms";
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-form/stop-point-workflow-detail-form-group";

@Component({
  selector: 'app-workflow-dialog',
  templateUrl: './add-stop-point-workflow.component.html',
})
export class AddStopPointWorkflowComponent implements OnInit {

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  constructor(
    public dialogRef: MatDialogRef<AddStopPointWorkflowComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddStopPointWorkflowDialogData,
    private notificationService: NotificationService,
    private userAdministrationService: UserAdministrationService,
    private stopPointWorkflowService: StopPointWorkflowService
  ) {
  }

  ngOnInit() {
    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup();
  }

  addWorkflow() {
    const workflow = this.stopPointToWorkflowInfo(this.data.stopPoint);
    workflow.workflowComment = this.form.controls.workflowComment.value!;
    workflow.ccEmails = this.form.controls.ccEmails.value!;
    this.stopPointWorkflowService.addStopPointWorkflow(workflow);
  }

  private stopPointToWorkflowInfo(stopPoint: ReadServicePointVersion): StopPointAddWorkflow {
    return {
      versionId: stopPoint.id!,
      sloid: stopPoint.sloid!,
      sboid: stopPoint.businessOrganisation!,
      designationOfficial: stopPoint.designationOfficial!,
      swissMunicipalityName: stopPoint.servicePointGeolocation!.swissLocation!.localityMunicipality?.localityName,
      swissCanton: stopPoint.servicePointGeolocation!.swissLocation!.canton!
    }
  }


  closeDialog() {
    this.dialogRef.close(true);
  }
}
