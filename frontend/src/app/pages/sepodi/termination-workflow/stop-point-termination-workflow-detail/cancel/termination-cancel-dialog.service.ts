import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { TerminationCancelFormGroup } from '../stop-point-termination-workflow-detail-form-group';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogData } from '../../../../../core/components/dialog/dialog.data';
import { TerminationCancelDetailDialogComponent } from './termination-cancel-detail-dialog/termination-cancel-detail-dialog.component';

export interface TerminationCancelDetailDialogData extends DialogData {
  workflowId: number;
  cancelComment: FormGroup<TerminationCancelFormGroup>;
}

@Injectable({
  providedIn: 'root',
})
export class TerminationCancelDialogService {
  private readonly dialog = inject(MatDialog);
  private dialogRef?: MatDialogRef<TerminationCancelDetailDialogComponent>;

  openDialog(
    workflowId: number,
    cancelComment: FormGroup<TerminationCancelFormGroup>
  ): Observable<boolean> {
    const dialogData: TerminationCancelDetailDialogData = {
      title: 'WORKFLOW.TERMINATION.CANCEL',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      cancelComment: cancelComment,
    };

    return this.open(dialogData);
  }

  private open(dialogData: TerminationCancelDetailDialogData) {
    this.dialogRef = this.dialog.open(TerminationCancelDetailDialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
