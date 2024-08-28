import {Component, OnInit} from '@angular/core';
import {
  ApplicationType,
  EditStopPointWorkflow,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
  StopPointPerson,
  StopPointWorkflowService,
  WorkflowStatus,
} from '../../../../api';
import {FormArray, FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {StopPointWorkflowDetailData} from './stop-point-workflow-detail-resolver.service';
import {NotificationService} from '../../../../core/notification/notification.service';
import {
  StopPointRejectWorkflowDialogService
} from '../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.service';
import {environment} from '../../../../../environments/environment';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, catchError, EMPTY, Observable, of, take} from 'rxjs';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from './detail-form/stop-point-workflow-detail-form-group';
import {DecisionStepperComponent} from './decision/decision-stepper/decision-stepper.component';
import {DialogService} from "../../../../core/components/dialog/dialog.service";
import {ValidationService} from "../../../../core/validation/validation.service";
import {PermissionService} from "../../../../core/auth/permission/permission.service";
import {
  StopPointRestartWorkflowDialogService
} from "../stop-point-restart-workflow-dialog/stop-point-restart-workflow-dialog.service";

@Component({
  selector: 'stop-point-workflow-detail',
  templateUrl: './stop-point-workflow-detail.component.html',
})
export class StopPointWorkflowDetailComponent implements OnInit {
  protected readonly WorkflowStatus = WorkflowStatus;
  protected readonly ApplicationType = ApplicationType;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private readonly dialog: MatDialog,
    private readonly stopPointWorkflowService: StopPointWorkflowService,
    private readonly notificationService: NotificationService,
    private readonly stopPointRejectWorkflowDialogService: StopPointRejectWorkflowDialogService,
    private readonly stopPointRestartWorkflowDialogService: StopPointRestartWorkflowDialogService,
    private dialogService: DialogService,
    private permissionService: PermissionService,
  ) {}

  public isFormEnabled$ = new BehaviorSubject<boolean>(false);

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  stopPoint!: ReadServicePointVersion;
  workflow!: ReadStopPointWorkflow;
  initWorkflow!: ReadStopPointWorkflow;
  oldDesignation?: string;
  isAtLeastSupervisor!: boolean;
  bavActionEnabled = environment.sepodiWorkflowBavActionEnabled;

  ngOnInit() {
    const workflowData: StopPointWorkflowDetailData = this.route.snapshot.data.workflow;
    this.workflow = workflowData.workflow;

    this.initWorkflow = this.workflow;
    this.isAtLeastSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Sepodi);

    const indexOfVersionInReview = workflowData.servicePoint.findIndex(
      (i) => i.id === this.workflow.versionId,
    )!;
    this.stopPoint = workflowData.servicePoint[indexOfVersionInReview];
    this.oldDesignation = this.getOldDesignation(workflowData.servicePoint, indexOfVersionInReview);

    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(this.workflow);
    this.form.disable();
  }

  getOldDesignation(
    servicePoint: ReadServicePointVersion[],
    indexOfVersionInReview: number,
  ): string {
    const versionsBeforeInReview = servicePoint.slice(0, indexOfVersionInReview);
    return (
      versionsBeforeInReview
        .filter((i) => i.stopPoint && i.status === Status.Validated)
        .map((i) => i.designationOfficial)
        .at(-1) ?? '-'
    );
  }

  startWorkflow() {
    this.stopPointWorkflowService.startStopPointWorkflow(this.workflow.id!).subscribe(() => {
      this._reloadDetail('WORKFLOW.NOTIFICATION.START.SUCCESS');
    });
  }

  rejectWorkflow() {
    this.stopPointRejectWorkflowDialogService.openDialog(this.workflow.id!, 'REJECT');
  }

  restartWorkflow() {
    this.stopPointRestartWorkflowDialogService.openDialog(this.workflow.id!, "RESTART")
  }

  cancelWorkflow() {
    this.stopPointRejectWorkflowDialogService.openDialog(this.workflow.id!, 'CANCEL');
  }

  openDecisionDialog() {
    const decisionDialogRef = this.dialog.open(DecisionStepperComponent, {
      data: this.workflow.id,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    decisionDialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((reload) => {
        if (reload) {
          this._reloadDetail('WORKFLOW.NOTIFICATION.VOTE.SUCCESS');
        }
      });
  }

  private _reloadDetail(msg: string) {
    this.router
      .navigate([], {
        relativeTo: this.route,
      })
      .then(() => {
        this.notificationService.success(msg);
        this.ngOnInit();
      });
  }

  toggleEdit() {
    // const examinantsFormArray = this.form.get('examinants') as FormArray;

    if (this.form?.enabled) {
      this.showConfirmationDialog();
    } else {
      this.enableForm();
      // this.disableFirstTwoExaminants(examinantsFormArray);
      ValidationService.validateForm(this.form!);
    }
  }

  public disableFirstTwoExaminants(examinantsFormArray: FormArray): void {
    if (examinantsFormArray.length > 0) {
      examinantsFormArray.at(0).disable();
      if (examinantsFormArray.length > 1) {
        examinantsFormArray.at(1).disable();
      }
    }
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(this.initWorkflow);
          this.disableForm();
        }
      });
  }

  disableForm(): void {
    this.form?.disable({ emitEvent: false });
    this.isFormEnabled$.next(false);
  }

  private enableForm(): void {
    this.form?.enable({ emitEvent: false });
    this.isFormEnabled$.next(true);
  }

  confirmLeave(): Observable<boolean> {
    if (this.form?.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  save() {
    ValidationService.validateForm(this.form!);
    if (this.form?.valid) {
      const updatedVersion: EditStopPointWorkflow = {
        ccEmails: this.form.controls.ccEmails.value ?? undefined,
        designationOfficial: this.form.controls.designationOfficial.value!,
        workflowComment: this.form.controls.workflowComment.value!,
        examinants: this.form.controls.examinants.getRawValue().map(
          (examinant) => examinant as StopPointPerson,
        ),
      };
      this.update(this.workflow.id!, updatedVersion);
    }
  }

  update(id: number, stopPointWorkflow: EditStopPointWorkflow) {
    this.stopPointWorkflowService
      .editStopPointWorkflow(id, stopPointWorkflow)
      .pipe(catchError(this.handleError))
      .subscribe((workflow) => {
        this.workflow = workflow;
        this.initWorkflow = workflow;
        this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(this.workflow);
        this.notificationService.success('WORKFLOW.NOTIFICATION.EDIT.SUCCESS');
        this.disableForm();
      });
  }

  private handleError = () => {
    this.enableForm();
    return EMPTY;
  };
}
