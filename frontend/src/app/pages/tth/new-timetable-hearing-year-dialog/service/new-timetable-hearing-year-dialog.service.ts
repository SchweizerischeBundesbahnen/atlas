import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NewTimetableHearingYearDialogComponent } from '../new-timetable-hearing-year-dialog.component';
import { NewTimetableHearingYearDialogData } from '../model/new-timetable-hearing-year-dialog.data';

@Injectable({
  providedIn: 'root',
})
export class NewTimetableHearingYearDialogService {
  private dialogRef?: MatDialogRef<NewTimetableHearingYearDialogComponent>;

  constructor(private newTimetableHearingYearDialog: MatDialog) {}

  openDialog(
    newTimetableHearingYearDialogData: NewTimetableHearingYearDialogData
  ): Observable<boolean> {
    this.dialogRef = this.newTimetableHearingYearDialog.open(
      NewTimetableHearingYearDialogComponent,
      {
        data: newTimetableHearingYearDialogData,
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      }
    );
    return this.dialogRef.afterClosed().pipe(map((value) => (value ? value : false)));
  }

  closeConfirmDialog(): void {
    this.dialogRef?.close();
    this.dialogRef = undefined;
  }
}
