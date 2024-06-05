import {Component, OnInit} from '@angular/core';
import {StopPointWorkflowService} from "../../../../api";
import {FormGroup} from "@angular/forms";
import {StopPointWorkflowDetailFormGroup} from "../detail-form/stop-point-workflow-detail-form-group";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {NotificationService} from "../../../../core/notification/notification.service";
import {Router} from "@angular/router";

@Component({
  selector: 'stop-point-workflow-overview',
  templateUrl: './stop-point-workflow-overview.component.html',
})
export class StopPointWorkflowOverviewComponent implements OnInit {

  constructor(
    private detailHelperService: DetailHelperService,
    private stopPointWorkflowService: StopPointWorkflowService,
    private notificationService: NotificationService,
    private router: Router,
  ) {
  }

  form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  ngOnInit() {
    console.log("init StopPointWorkflowOverviewComponent");
  }

}
