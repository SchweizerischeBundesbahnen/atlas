import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {StopPointRejectWorkflowDialogComponent} from "./stop-point-reject-workflow-dialog.component";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {StopPointRejectWorkflowDialogData} from "./stop-point-reject-workflow-dialog-data";

@Injectable({
  providedIn: 'root'
})
export class StopPointRejectWorkflowDialogService {

  private dialogRef?: MatDialogRef<StopPointRejectWorkflowDialogComponent>;

  constructor(private dialog: MatDialog) {
  }

  openDialog(workflowId: number): Observable<boolean> {
    const dialogData: StopPointRejectWorkflowDialogData = {
      title: 'WORKFLOW.BUTTON.REJECT',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId
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
