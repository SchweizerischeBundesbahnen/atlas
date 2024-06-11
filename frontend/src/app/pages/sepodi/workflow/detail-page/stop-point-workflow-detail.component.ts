import {Component, OnInit} from '@angular/core';
import {ReadServicePointVersion, ReadStopPointWorkflow, Status} from "../../../../api";
import {FormGroup} from "@angular/forms";
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-form/stop-point-workflow-detail-form-group";
import {ActivatedRoute} from "@angular/router";
import {StopPointWorkflowDetailData} from "./stop-point-workflow-detail-resolver.service";

@Component({
  selector: 'stop-point-workflow-detail',
  templateUrl: './stop-point-workflow-detail.component.html',
})
export class StopPointWorkflowDetailComponent implements OnInit {

  constructor(private route: ActivatedRoute) {
  }

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  stopPoint!: ReadServicePointVersion;
  workflow!: ReadStopPointWorkflow;
  oldDesignation?: string;

  ngOnInit() {
    const workflowData: StopPointWorkflowDetailData = this.route.snapshot.data.workflow;
    this.workflow = workflowData.workflow;

    const indexOfVersionInReview = workflowData.servicePoint.findIndex(i=> i.id===this.workflow.versionId)!;
    this.stopPoint = workflowData.servicePoint[indexOfVersionInReview];
    const versionsBeforeInReview = workflowData.servicePoint.slice(0, indexOfVersionInReview);
    this.oldDesignation = versionsBeforeInReview.filter(i => i.stopPoint && i.status === Status.Validated).map(i => i.designationOfficial).at(-1) ?? '-';

    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(this.workflow);
    this.form.disable();
  }

}
