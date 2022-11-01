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
import { NotificationService } from '../notification/notification.service';
import { DialogService } from '../components/dialog/dialog.service';
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
  isWorkflowFormEditable: boolean = false;
  isReadMode: boolean = false;
  workflow!: Workflow;

  workflowFormGroup: FormGroup<WorkflowFormGroup> = new FormGroup<WorkflowFormGroup>({
    comment: new FormControl('', [Validators.required, AtlasFieldLengthValidator.comments]),
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
    private readonly authService: AuthService,
    private readonly notificationService: NotificationService,
    private readonly dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.initWorkflowForm();
  }

  showWorflowForm() {
    this.isReadMode = false;
    this.isAddWorkflowButtonDisabled = true;
    this.isWorkflowFormEditable = true;
  }

  startWorflow() {
    console.log(this.workflowFormGroup.getRawValue());
    this.validateForm();
    if (this.workflowFormGroup.valid) {
      let workflowStart: Workflow;
      workflowStart = {
        businessObjectId: this.getBusinessObjectId(),
        description: this.getStringValue(this.lineRecord.businessOrganisation),
        swissId: this.getStringValue(this.lineRecord.slnid),
        workflowType: WorkflowTypeEnum.Line,
        workflowComment: this.getFormControlValue('comment'),
        client: this.populatePerson(),
      };
      this.workflowServise.startWorkflow(workflowStart).subscribe((workflow) => {
        this.workflow = workflow;
        this.isAddWorkflowButtonDisabled = true;
        this.isReadMode = true;
        this.isWorkflowFormEditable = false;
        this.initWorkflowForm();
        this.notificationService.success('WORKFLOW.NOTIFICATION.START.SUCCESS');
      });
    }
  }

  cancelWorkflow() {
    if (this.workflowFormGroup.dirty) {
      this.dialogService
        .confirm({
          title: 'DIALOG.DISCARD_CHANGES_TITLE',
          message: 'DIALOG.LEAVE_SITE',
        })
        .subscribe((confirmed) => {
          if (confirmed) {
            this.isAddWorkflowButtonDisabled = false;
            this.isReadMode = false;
            this.isWorkflowFormEditable = false;
            this.workflowFormGroup.reset();
            this.initWorkflowForm();
          }
        });
    } else {
      this.isAddWorkflowButtonDisabled = false;
      this.isReadMode = false;
      this.isWorkflowFormEditable = false;
    }
  }

  private getFormControlValue(controlName: string): string {
    if (this.workflowFormGroup.get(controlName)) {
      return this.workflowFormGroup.get(controlName)?.value;
    }
    return '';
  }

  private populatePerson() {
    return {
      firstName: this.getFormControlValue('firstName'),
      lastName: this.getFormControlValue('lastName'),
      mail: this.getFormControlValue('mail'),
      personFunction: this.getFormControlValue('function'),
    };
  }

  private getBusinessObjectId() {
    return this.lineRecord.id !== undefined ? this.lineRecord.id : NaN;
  }

  private validateForm() {
    Object.keys(this.workflowFormGroup.controls).forEach((field) => {
      const control = this.workflowFormGroup.get(field);
      if (control) {
        control.markAsTouched({ onlySelf: true });
      }
    });
  }

  private initWorkflowForm() {
    if (this.workflow) {
      this.isReadMode = true;
      this.isAddWorkflowButtonDisabled = true;
      this.isStartWorkflowButtonDisabled = true;
      this.workflowFormGroup.disable();
      this.populateStartWorkflowFormGroupReadMode(this.workflow);
    } else {
      let workflowsInProgress = this.getWorkflowsInProgress();
      if (workflowsInProgress.length === 0) {
        this.populateStartWorkflowFormGroup();
        this.workflowFormGroup.enable();
      } else if (workflowsInProgress.length === 1) {
        this.isReadMode = true;
        this.isAddWorkflowButtonDisabled = true;
        this.isStartWorkflowButtonDisabled = true;
        this.workflowFormGroup.disable();
        let workflowId = workflowsInProgress[0].workflowId;
        if (workflowId) {
          this.workflowServise.getWorkflow(workflowId).subscribe((workflow: Workflow) => {
            this.workflow = workflow;
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
    this.workflowFormGroup.controls['firstName'].setValue(firstName);
    this.workflowFormGroup.controls['lastName'].setValue(lastName);
    this.workflowFormGroup.controls['function'].setValue('');
    this.workflowFormGroup.controls['mail'].setValue(user.email);
  }

  //read mode
  private populateStartWorkflowFormGroupReadMode(workflow: Workflow) {
    this.workflowFormGroup.controls['comment'].setValue(workflow.workflowComment);
    this.workflowFormGroup.controls['firstName'].setValue(workflow.client?.firstName);
    this.workflowFormGroup.controls['lastName'].setValue(workflow.client?.lastName);
    this.workflowFormGroup.controls['function'].setValue(workflow.client?.personFunction);
    this.workflowFormGroup.controls['mail'].setValue(workflow.client?.mail);
  }

  private getStringValue(value: string | undefined) {
    return value !== undefined ? value : '';
  }
}
