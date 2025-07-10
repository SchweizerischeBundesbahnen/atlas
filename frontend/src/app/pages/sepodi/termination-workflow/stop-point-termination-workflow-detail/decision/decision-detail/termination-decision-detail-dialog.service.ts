import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TerminationDecisionDetailDialogComponent } from './termination-decision-detail-dialog.component';
import { FormGroup } from '@angular/forms';
import { DialogData } from 'src/app/core/components/dialog/dialog.data';
import { TerminationDecisionFormGroup } from '../../stop-point-termination-workflow-detail-form-group';

export interface TerminationDecisionDetailDialogData extends DialogData {
  workflowId: number;
  decision: FormGroup<TerminationDecisionFormGroup>;
}

@Injectable({ providedIn: 'root' })
export class TerminationDecisionDetailDialogService {
  private dialogRef?: MatDialogRef<TerminationDecisionDetailDialogComponent>;

  constructor(private dialog: MatDialog) {}

  openDialog(
    workflowId: number,
    decision: FormGroup<TerminationDecisionFormGroup>
  ): Observable<boolean> {
    const dialogData: TerminationDecisionDetailDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      decision: decision,
    };

    return this.open(dialogData);
  }

  private open(dialogData: TerminationDecisionDetailDialogData) {
    this.dialogRef = this.dialog.open(
      TerminationDecisionDetailDialogComponent,
      {
        data: dialogData,
        disableClose: true,
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      }
    );

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
