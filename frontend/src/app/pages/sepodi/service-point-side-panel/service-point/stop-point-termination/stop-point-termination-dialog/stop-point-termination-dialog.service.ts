import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { StopPointTerminationDialogComponent } from './stop-point-termination-dialog.component';
import { ReadServicePointVersion } from '../../../../../../api';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { StopPointTerminationDialogData } from './stop-point-termination-dialog-data';
import { Moment } from 'moment';

@Injectable({
  providedIn: 'root',
})
export class StopPointTerminationDialogService {
  private dialogRef?: MatDialogRef<StopPointTerminationDialogComponent>;

  constructor(private dialog: MatDialog) {}

  openDialog(
    stopPoint: ReadServicePointVersion,
    boTerminationDate: Moment
  ): Observable<boolean> {
    const dialogData: StopPointTerminationDialogData = {
      title: 'TERMINATION_WORKFLOW.DIALOG.START_TERMINATION_TITLE',
      message: '',
      cancelText: 'DIALOG.CANCEL',
      confirmText: 'COMMON.SAVE',
      versionId: stopPoint.id,
      sloid: stopPoint.sloid,
      boTerminationDate: boTerminationDate.toDate(),
    };

    return this.open(dialogData);
  }

  private open(dialogData: StopPointTerminationDialogData) {
    this.dialogRef = this.dialog.open(StopPointTerminationDialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.dialogRef.afterClosed().pipe(map((value) => value));
  }
}
