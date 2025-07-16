import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TerminationDecisionDetailDialogComponent } from './termination-decision-detail-dialog.component';
import { FormGroup } from '@angular/forms';
import { DialogData } from 'src/app/core/components/dialog/dialog.data';
import { TerminationDecisionFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import { TerminationDecision } from '../../../../../../api/model/terminationDecision';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

export interface TerminationDecisionDetailDialogData extends DialogData {
  workflowId: number;
  readOnly: boolean;
  examinant: TerminationDecisionPersonEnum;
  decision: FormGroup<TerminationDecisionFormGroup>;
}

@Injectable({ providedIn: 'root' })
export class TerminationDecisionDetailDialogService {
  private readonly dialog = inject(MatDialog);
  private dialogRef?: MatDialogRef<TerminationDecisionDetailDialogComponent>;

  openDialog(
    workflowId: number,
    readOnly: boolean,
    examinant: TerminationDecisionPersonEnum,
    decision: FormGroup<TerminationDecisionFormGroup>
  ): Observable<boolean> {
    const dialogData: TerminationDecisionDetailDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      readOnly: readOnly,
      examinant: examinant,
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
