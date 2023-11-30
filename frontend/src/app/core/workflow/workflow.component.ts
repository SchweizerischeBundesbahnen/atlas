import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { LineRecord } from './model/line-record';
import { LinesService, LineVersionWorkflow, WorkflowProcessingStatus } from '../../api';
import { NotificationService } from '../notification/notification.service';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { WorkflowDialogService } from './dialog/workflow-dialog.service';

@Component({
  selector: 'app-workflow [lineRecord]',
  templateUrl: './workflow.component.html',
  styleUrls: ['./workflow.component.scss'],
})
export class WorkflowComponent implements OnInit, OnChanges, OnDestroy {
  @Input() lineRecord!: LineRecord;
  @Input() descriptionForWorkflow!: string;

  @Output() workflowEvent = new EventEmitter<void>();

  workflowInProgress = false;
  workflowId: number | undefined;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly notificationService: NotificationService,
    private readonly translateService: TranslateService,
    private readonly lineService: LinesService,
    private readonly workflowDialogService: WorkflowDialogService,
  ) {}

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
    return lineVersionWorkflows.filter(
      (lvw) => lvw.workflowProcessingStatus === WorkflowProcessingStatus.InProgress,
    );
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  newWorkflow() {
    this.workflowDialogService
      .openNew(this.lineRecord, this.descriptionForWorkflow)
      .subscribe((workflowEvent) => {
        if (workflowEvent) {
          this.workflowEvent.emit();
        }
      });
  }

  openWorkflow() {
    this.workflowDialogService
      .openExisting(this.lineRecord, this.descriptionForWorkflow)
      .subscribe((workflowEvent) => {
        if (workflowEvent) {
          this.workflowEvent.emit();
        }
      });
  }

  skipWorkflow() {
    this.lineService
      .skipWorkflow(this.lineRecord.id!)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(() => {
        this.workflowEvent.emit();
      });
  }
}
