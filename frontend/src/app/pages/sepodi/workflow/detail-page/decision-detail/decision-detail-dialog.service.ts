import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {DecisionDetailDialogComponent} from "./decision-detail-dialog.component";
import {DialogData} from "../../../../../core/components/dialog/dialog.data";

export interface DecisionDetailDialogData extends DialogData {
  workflowId: number;
  examinantId: number;
}

@Injectable({providedIn: 'root'})
export class DecisionDetailDialogService {
  private dialogRef?: MatDialogRef<DecisionDetailDialogComponent>;

  constructor(private dialog: MatDialog) {
  }

  openDialog(workflowId: number, examinantId: number): Observable<boolean> {
    const dialogData: DecisionDetailDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      examinantId: examinantId,
    };

    return this.open(dialogData);
  }

  private open(dialogData: DecisionDetailDialogData) {
    this.dialogRef = this.dialog.open(DecisionDetailDialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
