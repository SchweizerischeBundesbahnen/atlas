import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NewTimetableHearingYearDialogComponent } from '../new-timetable-hearing-year-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class NewTimetableHearingYearDialogService {
  private dialogRef?: MatDialogRef<NewTimetableHearingYearDialogComponent>;

  constructor(private newTimetableHearingYearDialog: MatDialog) {}

  openDialog(): Observable<boolean> {
    this.dialogRef = this.newTimetableHearingYearDialog.open(
      NewTimetableHearingYearDialogComponent,
      {
        data: {},
        disableClose: true,
        width: '40%',
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      }
    );
    return this.dialogRef.afterClosed().pipe(map((value) => (value ? value : false)));
  }
}
