import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {StopPointAddWorkflow, StopPointPerson, StopPointWorkflowService,} from '../../../../api';
import {AddStopPointWorkflowDialogData} from './add-stop-point-workflow-dialog-data';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
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
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {UniqueEmailsValidator} from "../../../../core/validation/unique-emails-validator/unique-emails-validator";

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
    private router: Router,
    private fb: FormBuilder
  ) {}

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  ngOnInit() {
    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup();
    const listOfExaminants: StopPointPerson[] = this.data.examinants;
    const emptyExaminant: StopPointPerson = {
      firstName: '',
      lastName: '',
      organisation: '',
      mail: ''
    };
    listOfExaminants.push(emptyExaminant);
    this.form.setControl('examinants', this.createExaminantsFormArray(listOfExaminants));
    this.form.controls.designationOfficial.setValue(this.data.stopPoint.designationOfficial)
  }

  private createExaminantsFormArray(listOfExaminants: StopPointPerson[]): FormArray<FormGroup<ExaminantFormGroup>> {
    const formGroups = listOfExaminants.map(person => this.fb.group<ExaminantFormGroup>({
      id: new FormControl(person.id ?? null),
      firstName: new FormControl(person.firstName ?? null),
      lastName: new FormControl(person.lastName ?? null),
      organisation: new FormControl(person.organisation ?? '', Validators.required),
      personFunction: new FormControl(person.personFunction ?? null),
      mail: new FormControl(person.mail ?? '', [Validators.required, AtlasCharsetsValidator.email]),
      judgementIcon: new FormControl(null),
      judgement: new FormControl(person.judgement ?? null),
      decisionType: new FormControl(person.decisionType ?? null),
    }));
    return new FormArray<FormGroup<ExaminantFormGroup>>(formGroups);
  }

  addWorkflow() {
    this.form.controls.examinants.setValidators(UniqueEmailsValidator.uniqueEmails());
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const workflow: StopPointAddWorkflow = {
        applicantMail: this.userService.currentUser!.email,
        versionId: this.data.stopPoint.id!,
        sloid: this.data.stopPoint.sloid!,
        workflowComment: this.form.controls.workflowComment.value!,
        ccEmails: this.form.controls.ccEmails.value!,
        examinants: this.form.controls.examinants.value.map(examinant => examinant as StopPointPerson)
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
