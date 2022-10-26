import { Component, Input, OnInit } from '@angular/core';
import { LineRecord } from './line-record';
import { LineVersionWorkflow, WorkflowProcessingStatus, WorkflowService } from '../../api';

@Component({
  selector: 'app-workflow [lineRecord]',
  templateUrl: './workflow.component.html',
  styleUrls: ['./workflow.component.scss'],
})
export class WorkflowComponent implements OnInit {
  @Input() lineRecord!: LineRecord;

  constructor(private readonly workflowServise: WorkflowService) {}

  ngOnInit(): void {
    let lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.lineRecord.lineVersionWorkflows?.forEach((lvw) => lineVersionWorkflows.push(lvw));
    let workflowsInProgress = lineVersionWorkflows.filter(
      (lvw) => lvw.workflowProcessingStatus === WorkflowProcessingStatus.InProgress
    );
    if (workflowsInProgress.length === 1) {
      let workflowId = workflowsInProgress[0].workflowId;
      if (workflowId) {
        this.workflowServise.getWorkflow(workflowId).subscribe((response) => {
          console.log(response);
        });
      }
    }
  }
}
