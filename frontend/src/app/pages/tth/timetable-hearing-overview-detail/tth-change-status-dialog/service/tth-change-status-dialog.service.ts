import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TthChangeStatusDialogComponent } from '../tth-change-status-dialog.component';
import { ColumnDropDownEvent } from '../../../../../core/components/table/column-drop-down-event';
import { StatusChangeData } from '../model/status-change-data';

@Injectable({
  providedIn: 'root',
})
export class TthChangeStatusDialogService {
  private changeStatusDialog?: MatDialogRef<TthChangeStatusDialogComponent>;

  constructor(private dialog: MatDialog) {}

  onClick(changedStatus: ColumnDropDownEvent): Observable<boolean> {
    const statusChangeData: StatusChangeData = {
      title: 'TTH.DIALOG.STATUS_CHANGE',
      message: 'TTH.DIALOG.STATUS_CHANGE_MESSAGE',
      cancelText: 'TTH.DIALOG.BACK',
      confirmText: 'TTH.DIALOG.STATUS_CHANGE',
      ths: changedStatus.value,
    };
    this.changeStatusDialog = this.dialog.open(TthChangeStatusDialogComponent, {
      data: statusChangeData,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });

    return this.changeStatusDialog.afterClosed().pipe(map((value) => (value ? value : true)));
  }
}
