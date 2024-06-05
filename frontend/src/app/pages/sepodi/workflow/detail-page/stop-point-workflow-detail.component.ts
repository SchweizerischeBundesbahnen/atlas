import {Component, OnInit} from '@angular/core';
import {StopPointWorkflowService} from "../../../../api";
import {FormGroup} from "@angular/forms";
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-form/stop-point-workflow-detail-form-group";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {NotificationService} from "../../../../core/notification/notification.service";
import {Router} from "@angular/router";

@Component({
  selector: 'stop-point-workflow-detail',
  templateUrl: './stop-point-workflow-detail.component.html',
})
export class StopPointWorkflowDetailComponent implements OnInit {

  constructor(
    private detailHelperService: DetailHelperService,
    private stopPointWorkflowService: StopPointWorkflowService,
    private notificationService: NotificationService,
    private router: Router,
  ) {
  }

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  ngOnInit() {
    console.log("init workfl detail");
    this.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup();
  }

}
