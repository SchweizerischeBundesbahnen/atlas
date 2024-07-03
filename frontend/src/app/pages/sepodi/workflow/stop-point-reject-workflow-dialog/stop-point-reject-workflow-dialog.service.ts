import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {StopPointRejectWorkflowDialogComponent} from "./stop-point-reject-workflow-dialog.component";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {RejectType, StopPointRejectWorkflowDialogData} from "./stop-point-reject-workflow-dialog-data";

@Injectable({
  providedIn: 'root'
})
export class StopPointRejectWorkflowDialogService {

  private dialogRef?: MatDialogRef<StopPointRejectWorkflowDialogComponent>;

  constructor(private dialog: MatDialog) {
  }

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

  getTitle(rejectType: RejectType): string {
    if (rejectType === "REJECT") {
      return 'WORKFLOW.BUTTON.REJECT';
    }
    if (rejectType === "CANCEL") {
      return 'WORKFLOW.BUTTON.CANCEL';
    }
    throw Error('The given type does not exist!')
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
