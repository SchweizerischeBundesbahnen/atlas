import { Component, Input, OnInit } from '@angular/core';
import { LineRecord } from './line-record';
import {
  LineVersionWorkflow,
  Workflow,
  WorkflowProcessingStatus,
  WorkflowService,
} from '../../api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { WorkflowFormGroup } from './workflow-form-group';
import { AuthService } from '../auth/auth.service';
import { User } from '../components/user/user';
import { AtlasFieldLengthValidator } from '../validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../validation/charsets/atlas-charsets-validator';
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;

@Component({
  selector: 'app-workflow [lineRecord]',
  templateUrl: './workflow.component.html',
  styleUrls: ['./workflow.component.scss'],
})
export class WorkflowComponent implements OnInit {
  @Input() lineRecord!: LineRecord;
  isAddWorkflowButtonDisabled: boolean = false;
  isStartWorkflowButtonDisabled: boolean = false;
  showWorkflowData: boolean = false;
  workflow!: Workflow;

  workflowForm: FormGroup<WorkflowFormGroup> = new FormGroup<WorkflowFormGroup>({
    comment: new FormControl('', [Validators.maxLength(1500), Validators.required]),
    firstName: new FormControl('', [Validators.required, AtlasFieldLengthValidator.length_50]),
    lastName: new FormControl('', [Validators.required, AtlasFieldLengthValidator.length_50]),
    function: new FormControl('', [Validators.required, AtlasFieldLengthValidator.length_50]),
    mail: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_255,
      AtlasCharsetsValidator.email,
    ]),
  });

  constructor(
    private readonly workflowServise: WorkflowService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.initWorkflowForm();
  }

  showWorflow() {
    this.showWorkflowData = true;
    this.isAddWorkflowButtonDisabled = true;
  }

  startWorflow() {
    console.log(this.workflowForm.getRawValue());
    this.validateForm();
    if (this.workflowForm.valid) {
      let workflow: Workflow;
      workflow = {
        businessObjectId: this.lineRecord.id !== undefined ? this.lineRecord.id : NaN,
        description:
          this.lineRecord.businessOrganisation !== undefined
            ? this.lineRecord.businessOrganisation
            : '',
        swissId: this.lineRecord!.slnid !== undefined ? this.lineRecord!.slnid : '',
        workflowType: WorkflowTypeEnum.Line,
        workflowComment: this.workflowForm.controls['comment'].value,
        client: {
          firstName: this.workflowForm.controls['firstName'].value,
          lastName: this.workflowForm.controls['lastName'].value,
          mail: this.workflowForm.controls['mail'].value,
          personFunction: this.workflowForm.controls['function'].value,
        },
      };
      this.workflowServise.startWorkflow(workflow).subscribe((result) => {
        console.log(result);
        this.workflow = result;
        this.initWorkflowForm();
      });
    }
  }

  private validateForm() {
    Object.keys(this.workflowForm.controls).forEach((field) => {
      const control = this.workflowForm.get(field);
      if (control) {
        control.markAsTouched({ onlySelf: true });
      }
    });
  }

  private initWorkflowForm() {
    if (this.workflow) {
      this.showWorkflowData = true;
      this.isAddWorkflowButtonDisabled = true;
      this.isStartWorkflowButtonDisabled = true;
      this.workflowForm.disable();
      this.populateStartWorkflowFormGroupReadMode(this.workflow);
    } else {
      let workflowsInProgress = this.getWorkflowsInProgress();
      if (workflowsInProgress.length === 0) {
        this.populateStartWorkflowFormGroup();
        this.showWorkflowData = false;
        this.workflowForm.enable();
      } else if (workflowsInProgress.length === 1) {
        this.showWorkflowData = true;
        this.isAddWorkflowButtonDisabled = true;
        this.isStartWorkflowButtonDisabled = true;
        this.workflowForm.disable();
        let workflowId = workflowsInProgress[0].workflowId;
        if (workflowId) {
          this.workflowServise.getWorkflow(workflowId).subscribe((workflow: Workflow) => {
            this.populateStartWorkflowFormGroupReadMode(workflow);
          });
        }
      }
    }
  }

  private getWorkflowsInProgress() {
    let lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.lineRecord.lineVersionWorkflows?.forEach((lvw) => lineVersionWorkflows.push(lvw));
    return lineVersionWorkflows.filter(
      (lvw) => lvw.workflowProcessingStatus === WorkflowProcessingStatus.InProgress
    );
  }

  private populateStartWorkflowFormGroup() {
    let user: User = this.authService.claims;
    let userName = user?.name.substr(0, user.name.indexOf('(')).trim();
    let lastName = user?.name.substr(0, user.name.indexOf(' ')).trim();
    let firstName = userName.substr(userName.lastIndexOf(' '), userName.length).trim();
    this.workflowForm.controls['firstName'].setValue(firstName);
    this.workflowForm.controls['lastName'].setValue(lastName);
    this.workflowForm.controls['function'].setValue('');
    this.workflowForm.controls['mail'].setValue(user.email);
  }

  //read mode
  private populateStartWorkflowFormGroupReadMode(workflow: Workflow) {
    this.workflowForm.controls['comment'].setValue(workflow.workflowComment);
    this.workflowForm.controls['firstName'].setValue(workflow.client?.firstName);
    this.workflowForm.controls['lastName'].setValue(workflow.client?.lastName);
    this.workflowForm.controls['function'].setValue(workflow.client?.personFunction);
    this.workflowForm.controls['mail'].setValue(workflow.client?.mail);
  }
}
