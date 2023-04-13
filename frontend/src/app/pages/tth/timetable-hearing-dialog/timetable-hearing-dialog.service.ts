import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TimetableHearingDialogComponent } from './timetable-hearing-dialog.component';
import { TimetableHearingDialogData } from './timetable-hearing-dialog.data';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingDialogService {
  private confirmTimetableHearingDialog?: MatDialogRef<TimetableHearingDialogComponent>;

  constructor(private timetableHearingDialog: MatDialog) {}

  confirm(tthDialogData: TimetableHearingDialogData): Observable<boolean> {
    this.confirmTimetableHearingDialog = this.timetableHearingDialog.open(
      TimetableHearingDialogComponent,
      {
        data: tthDialogData,
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
        disableClose: true,
        autoFocus: true,
      }
    );
    return this.confirmTimetableHearingDialog
      .afterClosed()
      .pipe(map((value) => (value ? value : false)));
  }

  closeConfirmDialog(): void {
    this.confirmTimetableHearingDialog?.close();
    this.confirmTimetableHearingDialog = undefined;
  }
}
