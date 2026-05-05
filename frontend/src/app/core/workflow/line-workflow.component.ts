import { Component, inject, Input, OnChanges, OnInit, output } from '@angular/core';
import { LineRecord } from './model/line-record';
import { LineVersionWorkflow, WorkflowProcessingStatus } from '../../api';
import { LineInternalService } from '../../api/service/lidi/line-internal.service';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { DialogService } from '../components/dialog/dialog.service';
import { LineWorkflowDialogData } from './dialog/line-workflow-dialog-data';
import { LineWorkflowDialogComponent } from './dialog/line-workflow-dialog.component';

@Component({
  selector: 'atlas-workflow [lineRecord]',
  templateUrl: './line-workflow.component.html',
  styleUrls: ['./line-workflow.component.scss'],
  imports: [AtlasButtonComponent],
})
export class LineWorkflowComponent implements OnInit, OnChanges {
  private readonly lineInternalService = inject(LineInternalService);
  private readonly dialogService = inject(DialogService);

  @Input() lineRecord!: LineRecord;
  @Input() descriptionForWorkflow!: string;

  readonly workflowEvent = output<void>();

  workflowInProgress = false;
  workflowId: number | undefined;

  ngOnInit(): void {
    this.initWorkflowButtons();
  }

  ngOnChanges() {
    this.initWorkflowButtons();
  }

  initWorkflowButtons() {
    const workflowsInProgress = this.filterWorkflowsInProgress();
    if (workflowsInProgress.length === 0) {
      this.workflowInProgress = false;
    } else if (workflowsInProgress.length === 1) {
      const workflowId = workflowsInProgress[0].workflowId;
      if (workflowId) {
        this.workflowInProgress = true;
      }
    }
  }

  private filterWorkflowsInProgress() {
    const lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.lineRecord.lineVersionWorkflows?.forEach((lvw) => lineVersionWorkflows.push(lvw));
    return lineVersionWorkflows.filter((lvw) => lvw.workflowProcessingStatus === WorkflowProcessingStatus.InProgress);
  }

  newWorkflow() {
    const dialogData: LineWorkflowDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'WORKFLOW.BUTTON.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.START',
      lineRecord: this.lineRecord,
      descriptionForWorkflow: this.descriptionForWorkflow,
      number: this.lineRecord.number,
    };
    this.dialogService
      .openDialogDataWithConfirmationResult(dialogData, LineWorkflowDialogComponent)
      .subscribe((workflowEvent) => {
        if (workflowEvent) {
          this.workflowEvent.emit();
        }
      });
  }

  openWorkflow() {
    const dialogData: LineWorkflowDialogData = {
      title: 'WORKFLOW.TITLE',
      message: '',
      cancelText: 'COMMON.BACK',
      confirmText: 'WORKFLOW.BUTTON.START',
      lineRecord: this.lineRecord,
      descriptionForWorkflow: this.descriptionForWorkflow,
      number: this.lineRecord.number,
    };

    this.dialogService
      .openDialogDataWithConfirmationResult(dialogData, LineWorkflowDialogComponent)
      .subscribe((workflowEvent) => {
        if (workflowEvent) {
          this.workflowEvent.emit();
        }
      });
  }

  skipWorkflow() {
    this.lineInternalService.skipWorkflow(this.lineRecord.id!).subscribe(() => this.workflowEvent.emit());
  }
}
