import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { WorkflowDialogData } from './workflow-dialog-data';
import { NotificationService } from '../../notification/notification.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { WorkflowFormGroup } from '../workflow-form-group';
import { AtlasFieldLengthValidator } from '../../validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../validation/whitespace/whitespace-validator';
import { takeUntil } from 'rxjs/operators';
import {
  LineVersionWorkflow,
  UserAdministrationService,
  Workflow,
  WorkflowProcessingStatus,
  WorkflowService,
  WorkflowStart,
} from '../../../api';
import { TranslateService } from '@ngx-translate/core';
import { ValidationService } from '../../validation/validation.service';
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;

@Component({
  selector: 'app-workflow-dialog',
  templateUrl: './workflow-dialog.component.html',
  styleUrls: ['./workflow-dialog.component.scss'],
})
export class WorkflowDialogComponent implements OnInit, OnDestroy {
  workflowStartFormGroup: FormGroup<WorkflowFormGroup> = new FormGroup<WorkflowFormGroup>({
    comment: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.comments,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    firstName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    lastName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    function: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    mail: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_255,
      AtlasCharsetsValidator.email,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
  });

  workflowId?: number;
  workflowStatusTranslated!: string;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<WorkflowDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: WorkflowDialogData,
    private notificationService: NotificationService,
    private workflowService: WorkflowService,
    private userAdministrationService: UserAdministrationService,
    private translateService: TranslateService,
  ) {}

  ngOnInit() {
    this.initWorkflowForm();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  private initWorkflowForm() {
    const workflowsInProgress = this.filterWorkflowsInProgress();
    if (workflowsInProgress.length === 0) {
      this.populateUserDataFormFromAuthenticatedUser();
    } else if (workflowsInProgress.length === 1) {
      const workflowId = workflowsInProgress[0].workflowId;
      if (workflowId) {
        this.initWorkflowReadMode(workflowId);
      }
    }
  }

  private initWorkflowReadMode(workflowId: number) {
    this.workflowStartFormGroup.disable();
    this.workflowId = workflowId;

    this.workflowService
      .getWorkflow(workflowId)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((workflow: Workflow) => {
        this.populateWorkflowStartFormGroupFromPersistence(workflow);
      });
  }

  private filterWorkflowsInProgress() {
    const lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.data.lineRecord.lineVersionWorkflows?.forEach((lvw) => lineVersionWorkflows.push(lvw));
    return lineVersionWorkflows.filter(
      (lvw) => lvw.workflowProcessingStatus === WorkflowProcessingStatus.InProgress,
    );
  }

  private populateUserDataFormFromAuthenticatedUser() {
    this.workflowStartFormGroup.reset();
    this.userAdministrationService
      .getCurrentUser()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((user) => {
        this.workflowStartFormGroup.controls.firstName.setValue(user.firstName);
        this.workflowStartFormGroup.controls.lastName.setValue(user.lastName);
        this.workflowStartFormGroup.controls.mail.setValue(user.mail);
      });
  }

  private populateWorkflowStartFormGroupFromPersistence(workflow: Workflow) {
    this.getTranslatedWorkflowStatus(workflow);
    this.workflowStartFormGroup.controls.comment.setValue(workflow.workflowComment);
    this.workflowStartFormGroup.controls.firstName.setValue(workflow.client?.firstName);
    this.workflowStartFormGroup.controls.lastName.setValue(workflow.client?.lastName);
    this.workflowStartFormGroup.controls.function.setValue(workflow.client?.personFunction);
    this.workflowStartFormGroup.controls.mail.setValue(workflow.client?.mail);
  }

  getTranslatedWorkflowStatus(workflow: Workflow) {
    return this.translateService
      .get('WORKFLOW.STATUS.' + workflow.workflowStatus)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((translation) => {
        this.workflowStatusTranslated = translation;
      });
  }

  startWorkflow() {
    ValidationService.validateForm(this.workflowStartFormGroup);
    if (this.workflowStartFormGroup.valid) {
      const workflowStart = this.populateWorkflowStart();
      this.workflowService
        .startWorkflow(workflowStart)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.closeDialog();
          this.notificationService.success('WORKFLOW.NOTIFICATION.START.SUCCESS');
        });
    }
  }

  private populateWorkflowStart(): WorkflowStart {
    return {
      businessObjectId: this.data.lineRecord.id !== undefined ? this.data.lineRecord.id : NaN,
      swissId: this.data.lineRecord.slnid ?? '',
      workflowType: WorkflowTypeEnum.Line,
      description: this.data.descriptionForWorkflow,
      workflowComment: this.workflowStartFormGroup.value.comment!,
      client: {
        firstName: this.workflowStartFormGroup.value.firstName!,
        lastName: this.workflowStartFormGroup.value.lastName!,
        mail: this.workflowStartFormGroup.value.mail!,
        personFunction: this.workflowStartFormGroup.value.function!,
      },
    };
  }

  closeDialog() {
    this.dialogRef.close(true);
  }
}
