import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { WorkflowDialogComponent } from './workflow-dialog.component';
import { LineRecord } from '../model/line-record';
import { WorkflowDialogData } from './workflow-dialog-data';

@Injectable({ providedIn: 'root' })
export class WorkflowDialogService {
  private dialogRef?: MatDialogRef<WorkflowDialogComponent>;

  constructor(private dialog: MatDialog) {}

  openNew(
    lineRecord: LineRecord,
    descriptionForWorkflow: string
  ): Observable<boolean> {
    const dialogData: WorkflowDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'WORKFLOW.BUTTON.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.START',
      lineRecord: lineRecord,
      descriptionForWorkflow: descriptionForWorkflow,
      number: lineRecord.number,
    };

    return this.open(dialogData);
  }

  openExisting(
    lineRecord: LineRecord,
    descriptionForWorkflow: string
  ): Observable<boolean> {
    const dialogData: WorkflowDialogData = {
      title: 'WORKFLOW.TITLE',
      message: '',
      cancelText: 'COMMON.BACK',
      confirmText: 'WORKFLOW.BUTTON.START',
      lineRecord: lineRecord,
      descriptionForWorkflow: descriptionForWorkflow,
      number: lineRecord.number,
    };
    return this.open(dialogData);
  }

  private open(dialogData: WorkflowDialogData) {
    this.dialogRef = this.dialog.open(WorkflowDialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
