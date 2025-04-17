import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AddStopPointWorkflowDialogData } from './add-stop-point-workflow-dialog-data';
import { AddStopPointWorkflowComponent } from './add-stop-point-workflow.component';
import { ReadServicePointVersion } from '../../../../api';

@Injectable({ providedIn: 'root' })
export class AddStopPointWorkflowDialogService {
  private dialogRef?: MatDialogRef<AddStopPointWorkflowComponent>;

  constructor(private dialog: MatDialog) {}

  openDialog(stopPoint: ReadServicePointVersion): Observable<boolean> {
    const dialogData: AddStopPointWorkflowDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      stopPoint: stopPoint,
    };

    return this.open(dialogData);
  }

  private open(dialogData: AddStopPointWorkflowDialogData) {
    this.dialogRef = this.dialog.open(AddStopPointWorkflowComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
