import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AddExaminantsComponent } from './add-examinants.component';
import { AddExaminantsDialogData } from './add-examinants-dialog-data';

@Injectable({ providedIn: 'root' })
export class AddExaminantsDialogService {
  private dialogRef?: MatDialogRef<AddExaminantsComponent>;

  constructor(private dialog: MatDialog) {}

  openDialog(workflowId: number): Observable<boolean> {
    const dialogData: AddExaminantsDialogData = {
      workflowId: workflowId,
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
    };

    return this.open(dialogData);
  }

  private open(dialogData: AddExaminantsDialogData) {
    this.dialogRef = this.dialog.open(AddExaminantsComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
