import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogComponent } from './dialog.component';
import { DialogData } from './dialog.data';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private confirmDialog?: MatDialogRef<DialogComponent>;

  constructor(private dialog: MatDialog) {}

  confirm(dialogData: DialogData): Observable<boolean> {
    this.confirmDialog = this.dialog.open(DialogComponent, {
      data: dialogData,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    return this.confirmDialog.afterClosed().pipe(map((value) => (value ? value : false)));
  }

  closeConfirmDialog(): void {
    this.confirmDialog?.close();
    this.confirmDialog = undefined;
  }

  confirmLeave(showDialog: boolean, dialogRef: MatDialogRef<any>): void {
    if (!showDialog) {
      dialogRef.close();
      return;
    }
    this.confirm({
      title: 'DIALOG.DISCARD_CHANGES_TITLE',
      message: 'DIALOG.LEAVE_SITE',
    }).subscribe((result) => {
      if (result) {
        dialogRef.close();
      } else {
        this.closeConfirmDialog();
      }
    });
  }
}
