import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {DecisionDetailDialogComponent} from "./decision-detail-dialog.component";
import {DialogData} from "../../../../../core/components/dialog/dialog.data";
import {FormGroup} from "@angular/forms";
import {ExaminantFormGroup} from "../../detail-form/stop-point-workflow-detail-form-group";

export interface DecisionDetailDialogData extends DialogData {
  workflowId: number;
  examinant: FormGroup<ExaminantFormGroup>;
}

@Injectable({providedIn: 'root'})
export class DecisionDetailDialogService {
  private dialogRef?: MatDialogRef<DecisionDetailDialogComponent>;

  constructor(private dialog: MatDialog) {
  }

  openDialog(workflowId: number, examinant: FormGroup<ExaminantFormGroup>): Observable<boolean> {
    const dialogData: DecisionDetailDialogData = {
      title: 'WORKFLOW.BUTTON.ADD',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'WORKFLOW.BUTTON.SEND',
      workflowId: workflowId,
      examinant: examinant,
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
