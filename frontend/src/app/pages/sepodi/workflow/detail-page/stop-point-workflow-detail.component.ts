import { Component, OnInit } from '@angular/core';
import {
  ApplicationType,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
  StopPointWorkflowService,
  WorkflowStatus,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { StopPointWorkflowDetailData } from './stop-point-workflow-detail-resolver.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StopPointRejectWorkflowDialogService } from '../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.service';
import { environment } from '../../../../../environments/environment';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { take } from 'rxjs';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from './detail-form/stop-point-workflow-detail-form-group';
import { DecisionStepperComponent } from './decision/decision-stepper/decision-stepper.component';

@Component({
  selector: 'stop-point-workflow-detail',
  templateUrl: './stop-point-workflow-detail.component.html',
})
export class StopPointWorkflowDetailComponent implements OnInit {
  protected readonly WorkflowStatus = WorkflowStatus;
  protected readonly ApplicationType = ApplicationType;

  constructor(
    private route: ActivatedRoute,
    private readonly dialog: MatDialog,
    private readonly stopPointWorkflowService: StopPointWorkflowService,
    private readonly notificationService: NotificationService,
    private readonly stopPointRejectWorkflowDialogService: StopPointRejectWorkflowDialogService,
    protected readonly permissionService: PermissionService,
  ) {}

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  stopPoint!: ReadServicePointVersion;
  workflow!: ReadStopPointWorkflow;
  oldDesignation?: string;
  bavActionEnabled = environment.sepodiWorkflowBavActionEnabled;

  ngOnInit() {
    const workflowData: StopPointWorkflowDetailData = this.route.snapshot.data.workflow;
    this.workflow = workflowData.workflow;

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
    this.stopPointWorkflowService
      .startStopPointWorkflow(this.workflow.id!)
      .subscribe((startedWF) => {
        this.workflow = startedWF;
        this.notificationService.success('WORKFLOW.NOTIFICATION.START.SUCCESS');
      });
  }

  rejectWorkflow() {
    this.stopPointRejectWorkflowDialogService.openDialog(this.workflow.id!);
  }

  openDecisionDialog() {
    const decisionDialogRef = this.dialog.open(DecisionStepperComponent, {
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    const decisionDialogComponent = decisionDialogRef.componentInstance;
    const obtainOtpSubscription = this._registerObtainOtpHandler(decisionDialogComponent);
    const verifyPinSubscription = this._registerVerifyPinHandler(decisionDialogComponent);
    const sendDecisionSubscription = this._registerSendDecisionHandler(
      decisionDialogComponent,
      decisionDialogRef,
    );
    decisionDialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe(() => {
        obtainOtpSubscription.unsubscribe();
        verifyPinSubscription.unsubscribe();
        sendDecisionSubscription.unsubscribe();
      });
  }

  private _registerObtainOtpHandler(decisionDialogComponent: DecisionStepperComponent) {
    return decisionDialogComponent.obtainOtp.subscribe((stepData) => {
      stepData.swapLoading();
      this.stopPointWorkflowService
        .obtainOtp(this.workflow.id!, {
          examinantMail: stepData.mail.value,
        })
        .subscribe({
          next: () => {
            stepData.swapLoading();
            stepData.continue();
          },
          error: () => stepData.swapLoading(),
        });
    });
  }

  private _registerVerifyPinHandler(decisionDialogComponent: DecisionStepperComponent) {
    return decisionDialogComponent.verifyPin.subscribe((stepData) => {
      stepData.swapLoading();
      this.stopPointWorkflowService
        .verifyOtp(this.workflow.id!, {
          examinantMail: stepData.mail.value,
          pinCode: stepData.pin.value,
        })
        .subscribe({
          next: (examinant) => {
            stepData.swapLoading();
            stepData.continue(examinant);
          },
          error: () => stepData.swapLoading(),
        });
    });
  }

  private _registerSendDecisionHandler(
    decisionDialogComponent: DecisionStepperComponent,
    decisionDialogRef: MatDialogRef<DecisionStepperComponent>,
  ) {
    return decisionDialogComponent.sendDecision.subscribe((stepData) => {
      stepData.swapLoading();
      this.stopPointWorkflowService
        .voteWorkflow(this.workflow.id!, stepData.verifiedExaminant.id!, stepData.decision)
        .subscribe({
          next: () => {
            decisionDialogRef.close();
            this.notificationService.success('WORKFLOW.NOTIFICATION.VOTE.SUCCESS');
          },
          error: () => stepData.swapLoading(),
        });
    });
  }
}
