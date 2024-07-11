import { Injectable } from '@angular/core';
import {
  RejectType,
  StopPointRejectWorkflowDialogData
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog-data";
import {Observable} from "rxjs";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {StopPointRestartWorkflowDialogComponent} from "./stop-point-restart-workflow-dialog.component";
import {
  StopPointRejectWorkflowDialogComponent
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.component";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class StopPointRestartWorkflowDialogService {
  private dialogRef?: MatDialogRef<StopPointRestartWorkflowDialogComponent>;

  constructor(private dialog: MatDialog) { }

  openDialog(workflowId: number, rejectType: RejectType): Observable<boolean> {
    const dialogData: StopPointRejectWorkflowDialogData = {
      title: this.getTitle(rejectType),
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: this.getTitle(rejectType),
      workflowId: workflowId,
      rejectType: rejectType
    };
    return this.open(dialogData);
  }

  private open(dialogData: StopPointRejectWorkflowDialogData) {
    this.dialogRef = this.dialog.open(StopPointRejectWorkflowDialogComponent, {
      data: dialogData,
      minWidth: '50vw',
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
