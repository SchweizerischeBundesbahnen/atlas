import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { TerminationAbortFormGroup } from '../stop-point-termination-workflow-detail-form-group';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogData } from '../../../../../core/components/dialog/dialog.data';
import { TerminationAbortDetailDialogComponent } from './termination-abort-detail-dialog/termination-abort-detail-dialog.component';

export interface TerminationAbortDetailDialogData extends DialogData {
  workflowId: number;
  abortComment: FormGroup<TerminationAbortFormGroup>;
}

@Injectable({
  providedIn: 'root',
})
export class TerminationAbortDialogService {
  private readonly dialog = inject(MatDialog);
  private dialogRef?: MatDialogRef<TerminationAbortDetailDialogComponent>;

  openDialog(
    workflowId: number,
    abortComment: FormGroup<TerminationAbortFormGroup>
  ): Observable<boolean> {
    const dialogData: TerminationAbortDetailDialogData = {
      title: 'WORKFLOW.TERMINATION.CANCEL',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      abortComment: abortComment,
    };

    return this.open(dialogData);
  }

  private open(dialogData: TerminationAbortDetailDialogData) {
    this.dialogRef = this.dialog.open(TerminationAbortDetailDialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
