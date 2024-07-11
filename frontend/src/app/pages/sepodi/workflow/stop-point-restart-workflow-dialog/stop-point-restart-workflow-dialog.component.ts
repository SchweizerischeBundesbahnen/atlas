import {Component, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {StopPointRestartWorkflowFormGroup} from "./stop-point-restart-workflow-form-group";
import {
  StopPointRejectWorkflowFormGroupBuilder
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-form-group";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-stop-point-restart-workflow-dialog',
  templateUrl: './stop-point-restart-workflow-dialog.component.html'
})
export class StopPointRestartWorkflowDialogComponent implements OnInit {

  formGroup!: FormGroup<StopPointRestartWorkflowFormGroup>;


  constructor(
    public dialogRef: MatDialogRef<StopPointRestartWorkflowDialogComponent>,
    private detailHelperService: DetailHelperService,
  ) {
  }

  ngOnInit(): void {
  }

  closeDialog() {
    this.detailHelperService.confirmLeaveDirtyForm(this.formGroup).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }
}
