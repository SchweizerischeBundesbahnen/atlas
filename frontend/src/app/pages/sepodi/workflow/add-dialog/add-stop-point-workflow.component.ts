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
      console.log("adding ", workflow);
      this.stopPointWorkflowService.addStopPointWorkflow(workflow).subscribe(()=>{

      })
    }    else {
      console.log("form not valid ", this.form);
    }
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

  cancel() {
    this.detailHelperService.confirmLeaveDirtyForm(this.form).subscribe(confirmed => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }
}
