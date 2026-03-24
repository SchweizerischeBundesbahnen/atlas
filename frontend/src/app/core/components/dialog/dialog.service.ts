import { inject, Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, take } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogData } from './dialog.data';
import { ComponentType } from '@angular/cdk/portal';
import { DialogComponent } from './dialog.component';

@Injectable({
  providedIn: 'root',
  deps: [DialogService],
})
export class DialogService {
  private readonly matDialog = inject(MatDialog);

  openDialogDataWithConfirmationResult<D extends DialogData>(
    dialogData: D,
    dialogComponent: ComponentType<unknown> = DialogComponent
  ): Observable<boolean> {
    const dialogRef = this.matDialog.open(dialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    return dialogRef.afterClosed().pipe(
      take(1),
      map((value) => (value ? value : false))
    );
  }

  openDialogDataWithCustomResult<D extends DialogData, R>(
    dialogData: D,
    dialogComponent: ComponentType<unknown> = DialogComponent
  ): Observable<R | undefined> {
    const dialogRef = this.matDialog.open(dialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    return dialogRef.afterClosed().pipe(take(1));
  }

  openCustomDataWithConfirmationResult<T, D>(
    data: D,
    dialogComponent: ComponentType<T>
  ): Observable<boolean> {
    const dialogRef = this.matDialog.open(dialogComponent, {
      data,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    return dialogRef.afterClosed().pipe(
      take(1),
      map((value) => (value ? value : false))
    );
  }

  openWithoutResult<T, D extends DialogData>(
    dialogComponent: ComponentType<T>,
    dialogData: D
  ) {
    this.matDialog.open(dialogComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
  }

  confirmLeave(): Observable<boolean> {
    return this.openDialogDataWithConfirmationResult({
      title: 'DIALOG.DISCARD_CHANGES_TITLE',
      message: 'DIALOG.LEAVE_SITE',
    });
  }

  showInfo(dialogData: DialogData): Observable<boolean> {
    dialogData.isInfo = true;
    return this.openDialogDataWithConfirmationResult(dialogData);
  }
}
