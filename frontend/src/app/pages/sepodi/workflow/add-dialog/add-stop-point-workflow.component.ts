import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NotificationService} from "../../../../core/notification/notification.service";
import {UserAdministrationService, WorkflowService} from "../../../../api";
import {TranslateService} from "@ngx-translate/core";
import {AddStopPointWorkflowDialogData} from "./add-stop-point-workflow-dialog-data";

@Component({
  selector: 'app-workflow-dialog',
  templateUrl: './add-stop-point-workflow.component.html',
})
export class AddStopPointWorkflowComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddStopPointWorkflowComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddStopPointWorkflowDialogData,
    private notificationService: NotificationService,
    private workflowService: WorkflowService,
    private userAdministrationService: UserAdministrationService,
    private translateService: TranslateService,
  ) {}

  ngOnInit() {
  }

  addWorkflow() {
    console.log("adding workflow to backend ... todo");
  }


  closeDialog() {
    this.dialogRef.close(true);
  }
}
