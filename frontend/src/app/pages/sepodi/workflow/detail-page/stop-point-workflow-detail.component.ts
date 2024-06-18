import { Component, OnInit } from '@angular/core';
import {
  ApplicationType, EditStopPointWorkflow,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
  StopPointWorkflowService,
  WorkflowStatus,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import { StopPointWorkflowDetailData } from './stop-point-workflow-detail-resolver.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StopPointRejectWorkflowDialogService } from '../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.service';
import { environment } from '../../../../../environments/environment';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import {BehaviorSubject, catchError, EMPTY, Observable, of, take} from 'rxjs';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from './detail-form/stop-point-workflow-detail-form-group';
import { DecisionStepperComponent } from './decision/decision-stepper/decision-stepper.component';
import {Pages} from "../../../pages";
import {DialogService} from "../../../../core/components/dialog/dialog.service";
import {ValidationService} from "../../../../core/validation/validation.service";

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
    private dialogService: DialogService,

  ) {}

  public isFormEnabled$ = new BehaviorSubject<boolean>(false);

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
        this.reloadDetail();
      });
  }

  rejectWorkflow() {
    this.stopPointRejectWorkflowDialogService.openDialog(this.workflow.id!, "REJECT")
  }

  cancelWorkflow() {
    this.stopPointRejectWorkflowDialogService.openDialog(this.workflow.id!, "CANCEL");
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
            this.reloadDetail();
          },
          error: () => stepData.swapLoading(),
        });
    });
  }

  private reloadDetail(){
    this.router.navigateByUrl('/').then(() => {
      this.router
        .navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, this.workflow.id])
        .then(() => {});
    });
  }
  toggleEdit() {
    if (this.form?.enabled) {
      this.showConfirmationDialog();
    } else {
      this.enableForm();

    }
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.disableForm();
        }
      });
  }

  private disableForm(): void {
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
      let updatedVersion: EditStopPointWorkflow = {
        designationOfficial: this.form.controls.designationOfficial.value!,
        workflowComment: this.form.controls.workflowComment.value!
      }
      this.update(this.workflow.id!, updatedVersion);
    }
  }

  update(id: number, stopPointWorkflow: EditStopPointWorkflow ){
    this.stopPointWorkflowService.editStopPointWorkflow(id, stopPointWorkflow)
      .pipe(catchError(this.handleError))
      .subscribe((workflow) => {
        console.log("test ", workflow)
        this.workflow = workflow;
        this.notificationService.success("Erfolgreich");
        this.disableForm();
      });
  }

  private handleError = () => {
    this.enableForm();
    return EMPTY;
  };

  //TODO: Save designation Official in Sepodi
}
