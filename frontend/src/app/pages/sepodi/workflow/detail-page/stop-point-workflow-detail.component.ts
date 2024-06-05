import {Component, OnInit} from '@angular/core';
import {ReadServicePointVersion, ReadStopPointWorkflow, ServicePointsService} from "../../../../api";
import {FormGroup} from "@angular/forms";
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-form/stop-point-workflow-detail-form-group";
import {ActivatedRoute, Router} from "@angular/router";
import {StopPointWorkflowDetailData} from "./stop-point-workflow-detail-resolver.service";

@Component({
  selector: 'stop-point-workflow-detail',
  templateUrl: './stop-point-workflow-detail.component.html',
})
export class StopPointWorkflowDetailComponent implements OnInit {

  constructor(private router: Router, private route: ActivatedRoute,
              private servicePointService: ServicePointsService) {
  }

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  stopPoint!: ReadServicePointVersion;
  workflow!: ReadStopPointWorkflow;

  ngOnInit() {
    const workflowData: StopPointWorkflowDetailData = this.route.snapshot.data.workflow;
    this.workflow = workflowData.workflow;
    this.stopPoint = workflowData.version;

    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(this.workflow);
    this.form.disable();
  }

}
