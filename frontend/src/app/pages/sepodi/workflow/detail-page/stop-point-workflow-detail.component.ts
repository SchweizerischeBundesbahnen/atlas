import {Component, OnInit} from '@angular/core';
import {ReadServicePointVersion, ReadStopPointWorkflow, Status, StopPointWorkflowService, WorkflowStatus,} from '../../../../api';
import {FormGroup} from '@angular/forms';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from '../detail-form/stop-point-workflow-detail-form-group';
import {ActivatedRoute} from '@angular/router';
import {StopPointWorkflowDetailData} from './stop-point-workflow-detail-resolver.service';
import {NotificationService} from '../../../../core/notification/notification.service';
import {
  StopPointRejectWorkflowDialogService
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.service";
import {environment} from "../../../../../environments/environment";
import {MatDialog} from '@angular/material/dialog';
import {DecisionDialogComponent} from './decision-dialog/decision-dialog.component';

@Component({
  selector: 'stop-point-workflow-detail',
  templateUrl: './stop-point-workflow-detail.component.html',
})
export class StopPointWorkflowDetailComponent implements OnInit {
  protected readonly WorkflowStatus = WorkflowStatus;

  constructor(
    private route: ActivatedRoute,
    private readonly dialog: MatDialog,
    private readonly stopPointWorkflowService: StopPointWorkflowService,
    private readonly notificationService: NotificationService,
    private readonly stopPointRejectWorkflowDialogService: StopPointRejectWorkflowDialogService
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
    this.stopPointRejectWorkflowDialogService.openDialog(this.workflow.id!)
  }

  openDecisionDialog() {
    this.dialog.open(DecisionDialogComponent, {
      data: {},
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
  }
}
