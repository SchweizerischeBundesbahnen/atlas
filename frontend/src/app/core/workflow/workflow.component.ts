import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { LineRecord } from './model/line-record';
import {
  LinesService,
  LineVersionWorkflow,
  UserAdministrationService,
  Workflow,
  WorkflowProcessingStatus,
  WorkflowService,
  WorkflowStart,
} from '../../api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { WorkflowFormGroup } from './workflow-form-group';
import { AtlasFieldLengthValidator } from '../validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../validation/charsets/atlas-charsets-validator';
import { NotificationService } from '../notification/notification.service';
import { DialogService } from '../components/dialog/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { WorkflowEvent } from './model/workflow-event';
import { Observable, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Record } from '../components/base-detail/record';
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;
import { ValidationService } from '../validation/validation.service';

@Component({
  selector: 'app-workflow [lineRecord]',
  templateUrl: './workflow.component.html',
  styleUrls: ['./workflow.component.scss'],
})
export class WorkflowComponent implements OnInit, OnDestroy {
  @Input() lineRecord!: LineRecord;
  @Input() descriptionForWorkflow!: string;
  @Output() workflowEvent = new EventEmitter<WorkflowEvent>();
  @Input() switchVersionEvent!: Observable<Record>;
  isAddWorkflowButtonDisabled = false;
  isWorkflowFormEditable = false;
  isReadMode = false;
  workflowStatusTranslated!: string;
  workflowId: number | undefined;

  workflowFormGroup: FormGroup<WorkflowFormGroup> = new FormGroup<WorkflowFormGroup>({
    comment: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.comments,
      AtlasCharsetsValidator.iso88591,
    ]),
    firstName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
    ]),
    lastName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
    ]),
    function: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
    ]),
    mail: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_255,
      AtlasCharsetsValidator.email,
      AtlasCharsetsValidator.iso88591,
    ]),
  });
  private eventsSubscription!: Subscription;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly workflowService: WorkflowService,
    private readonly userAdministrationService: UserAdministrationService,
    private readonly notificationService: NotificationService,
    private readonly dialogService: DialogService,
    private readonly translateService: TranslateService,
    private readonly lineService: LinesService
  ) {}

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit(): void {
    this.eventsSubscription = this.switchVersionEvent
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((record) => {
        this.reloadWorkflowAfterSwitchEvent(record);
      });
    this.initWorkflowForm();
  }

  showWorflowForm() {
    this.isAddWorkflowButtonDisabled = true;
    this.isWorkflowFormEditable = true;
    this.initWorkflowForm();
  }

  startWorflow() {
    ValidationService.validateForm(this.workflowFormGroup);
    if (this.workflowFormGroup.valid) {
      const workflowStart: WorkflowStart = this.populateWorkflowStart();
      this.workflowService
        .startWorkflow(workflowStart)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((workflow) => {
          this.isAddWorkflowButtonDisabled = true;
          this.workflowFormGroup.disable();
          this.isReadMode = true;
          this.isWorkflowFormEditable = false;
          if (this.lineRecord.slnid) {
            this.lineService.getLineVersions(this.lineRecord.slnid).subscribe((lineVersion) => {
              const lineVersionUpdated = lineVersion.filter(
                (value) => value.id === this.lineRecord.id
              );
              this.lineRecord.lineVersionWorkflows = lineVersionUpdated[0].lineVersionWorkflows;
              this.initWorkflowForm();
            });
          }
          this.eventReloadParent();
          this.getTranslatedWorkflowStatus(workflow);
          this.notificationService.success('WORKFLOW.NOTIFICATION.START.SUCCESS');
        });
    }
  }

  toggleWorkflow() {
    if (this.workflowFormGroup.dirty) {
      this.leaveWorkflowEditionWhenForIsDirty();
    } else {
      this.resetToAddWorkflow();
    }
  }

  getTranslatedWorkflowStatus(workflow: Workflow) {
    return this.translateService
      .get('WORKFLOW.STATUS.' + workflow.workflowStatus)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((translation) => {
        this.workflowStatusTranslated = translation;
      });
  }

  eventReloadParent() {
    const workflowEvent: WorkflowEvent = {
      reload: true,
    };
    this.workflowEvent.emit(workflowEvent);
  }

  private populateWorkflowStart() {
    return {
      businessObjectId: this.getBusinessObjectId(),
      swissId: this.getStringValue(this.lineRecord.slnid),
      workflowType: WorkflowTypeEnum.Line,
      description: this.descriptionForWorkflow,
      workflowComment: this.getFormControlValue('comment'),
      client: this.populatePerson(),
    };
  }

  private resetToAddWorkflow() {
    this.workflowFormGroup.reset();
    this.isAddWorkflowButtonDisabled = false;
    this.isReadMode = false;
    this.isWorkflowFormEditable = false;
  }

  private leaveWorkflowEditionWhenForIsDirty() {
    this.dialogService.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        this.resetToAddWorkflow();
        this.initWorkflowForm();
      }
    });
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
    this.isReadMode = true;
    this.isAddWorkflowButtonDisabled = true;
    this.workflowFormGroup.disable();
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
    this.lineRecord.lineVersionWorkflows?.forEach((lvw) => lineVersionWorkflows.push(lvw));
    return lineVersionWorkflows.filter(
      (lvw) => lvw.workflowProcessingStatus === WorkflowProcessingStatus.InProgress
    );
  }

  private populateUserDataFormFromAuthenticatedUser() {
    this.workflowFormGroup.reset();
    this.userAdministrationService
      .getCurrentUser()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((user) => {
        this.workflowFormGroup.controls.firstName.setValue(user.firstName);
        this.workflowFormGroup.controls.lastName.setValue(user.lastName);
        this.workflowFormGroup.controls.mail.setValue(user.mail);
      });
  }

  //read mode
  private populateWorkflowStartFormGroupFromPersistence(workflow: Workflow) {
    this.getTranslatedWorkflowStatus(workflow);
    this.workflowFormGroup.controls.comment.setValue(workflow.workflowComment);
    this.workflowFormGroup.controls.firstName.setValue(workflow.client?.firstName);
    this.workflowFormGroup.controls.lastName.setValue(workflow.client?.lastName);
    this.workflowFormGroup.controls.function.setValue(workflow.client?.personFunction);
    this.workflowFormGroup.controls.mail.setValue(workflow.client?.mail);
  }

  private getStringValue(value: string | undefined) {
    return value ?? '';
  }

  //this method will be extremly simplified as soon as we migrate to Pages instead of Modal Dialog!!
  private reloadWorkflowAfterSwitchEvent(record: Record) {
    //reset all
    this.isReadMode = false;
    this.isAddWorkflowButtonDisabled = false;
    this.isWorkflowFormEditable = false;
    this.workflowFormGroup.enable();
    this.lineRecord = record;
    const workflowsInProgress = this.filterWorkflowsInProgress();
    if (workflowsInProgress.length === 0) {
      //show only add workflow button
      this.isAddWorkflowButtonDisabled = false;
      this.isReadMode = false;
    } else if (workflowsInProgress.length === 1) {
      //show workflow expansion-panel
      this.isReadMode = true;
      this.isAddWorkflowButtonDisabled = true;
      this.isWorkflowFormEditable = false;
      this.initWorkflowForm();
    }
  }
}
