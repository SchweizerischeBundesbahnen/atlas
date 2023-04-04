import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TthDialogComponent } from './tthdialog.component';
import { TthDialogData } from './tthdialog.data';

@Injectable({
  providedIn: 'root',
})
export class TthDialogService {
  private confirmTthDialog?: MatDialogRef<TthDialogComponent>;

  constructor(private tthDialog: MatDialog) {}

  confirm(tthDialogData: TthDialogData): Observable<boolean> {
    this.confirmTthDialog = this.tthDialog.open(TthDialogComponent, {
      data: tthDialogData,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    return this.confirmTthDialog.afterClosed().pipe(map((value) => (value ? value : false)));
  }

  // closeConfirmTthDialog(): void {
  //   this.confirmTthDialog?.close();
  //   this.confirmTthDialog = undefined;
  // }
  //
  // confirmLeave(): Observable<boolean> {
  //   return this.confirm({
  //     title: 'DIALOG.DISCARD_CHANGES_TITLE',
  //     column: '',
  //   });
  // }
}
