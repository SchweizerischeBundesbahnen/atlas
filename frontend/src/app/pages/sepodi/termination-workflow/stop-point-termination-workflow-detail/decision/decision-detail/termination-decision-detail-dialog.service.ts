import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TerminationDecisionDetailDialogComponent } from './termination-decision-detail-dialog.component';
import { FormGroup } from '@angular/forms';
import { DialogData } from 'src/app/core/components/dialog/dialog.data';
import { WorkflowStatus } from '../../../../../../api';
import { TerminationDecisionFormGroup } from './termination-decision-form-group';

export interface DecisionDetailDialogData extends DialogData {
  workflowId: number;
  workflowStatus: WorkflowStatus;
  decision: FormGroup<TerminationDecisionFormGroup>;
}

@Injectable({ providedIn: 'root' })
export class TerminationDecisionDetailDialogService {
  private dialogRef?: MatDialogRef<TerminationDecisionDetailDialogComponent>;

  constructor(private dialog: MatDialog) {}

  openDialog(
    workflowId: number,
    workflowStatus: WorkflowStatus,
    decision: FormGroup<TerminationDecisionFormGroup>
  ): Observable<boolean> {
    const dialogData: DecisionDetailDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      workflowStatus: workflowStatus,
      decision: decision,
    };

    return this.open(dialogData);
  }

  private open(dialogData: DecisionDetailDialogData) {
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
