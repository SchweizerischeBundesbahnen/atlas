import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TthChangeCantonDialogComponent } from '../tth-change-canton-dialog.component';
import { SwissCanton, TimetableHearingStatement } from '../../../../../api';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ChangeCantonData } from '../model/change-canton-data';

@Injectable({
  providedIn: 'root',
})
export class TthChangeCantonDialogService {
  private changeStatusDialog?: MatDialogRef<TthChangeCantonDialogComponent>;

  constructor(private dialog: MatDialog) {}

  onClick(changedCanton: SwissCanton, tths: TimetableHearingStatement[]): Observable<boolean> {
    const changeCantonData: ChangeCantonData = {
      title: 'TTH.STATEMENT.DIALOG.TITLE',
      message: 'TTH.DIALOG.MULTIPLE_STATUS_CHANGE_MESSAGE',
      cancelText: 'TTH.DIALOG.BACK',
      confirmText: 'TTH.DIALOG.CANTON_CHANGE',
      tths: tths,
      swissCanton: changedCanton,
    };
    this.changeStatusDialog = this.dialog.open(TthChangeCantonDialogComponent, {
      data: changeCantonData,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.changeStatusDialog.afterClosed().pipe(map((value) => (value ? value : true)));
  }
}
