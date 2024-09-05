import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {StopPointAddWorkflow, StopPointPerson, StopPointWorkflowService,} from '../../../../api';
import {AddStopPointWorkflowDialogData} from './add-stop-point-workflow-dialog-data';
import {FormArray, FormGroup} from '@angular/forms';
import {ValidationService} from '../../../../core/validation/validation.service';
import {DetailHelperService} from '../../../../core/detail/detail-helper.service';
import {NotificationService} from '../../../../core/notification/notification.service';
import {Router} from '@angular/router';
import {Pages} from '../../../pages';
import {UserService} from "../../../../core/auth/user/user.service";
import {
  ExaminantFormGroup,
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-page/detail-form/stop-point-workflow-detail-form-group";

@Component({
  selector: 'app-workflow-dialog',
  templateUrl: './add-stop-point-workflow.component.html',
  styleUrls: ['./add-stop-point-workflow.component.scss']
})
export class AddStopPointWorkflowComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddStopPointWorkflowComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddStopPointWorkflowDialogData,
    private detailHelperService: DetailHelperService,
    private stopPointWorkflowService: StopPointWorkflowService,
    private notificationService: NotificationService,
    private userService: UserService,
    private router: Router
  ) {}

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  ngOnInit() {
    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup();
    this.form.controls.designationOfficial.setValue(this.data.stopPoint.designationOfficial)
  }

  transformExaminants(examinants: FormArray<FormGroup<ExaminantFormGroup>>): StopPointPerson[] {
    return examinants.controls
      .filter(control => !control.disabled)
      .map(examinant => {
        if (examinant.controls.firstName.value === '') {
          examinant.controls.firstName.setValue(null);
        }
        if (examinant.controls.lastName.value === '') {
          examinant.controls.lastName.setValue(null);
        }
        return examinant.value as StopPointPerson;
      });
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
        examinants: this.transformExaminants(this.form.controls.examinants as FormArray<FormGroup>)
      }
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
