import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Observable, take } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogData } from './dialog.data';
import { ComponentType } from '@angular/cdk/portal';
import { DialogComponent } from './dialog.component';

const basicDialogConfig: Readonly<Pick<MatDialogConfig, 'disableClose' | 'panelClass' | 'backdropClass'>> = {
  disableClose: true,
  panelClass: 'atlas-dialog-panel',
  backdropClass: 'atlas-dialog-backdrop',
};

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private readonly matDialog = inject(MatDialog);

  openDialogDataWithConfirmationResult<D extends DialogData>(
    dialogData: D,
    dialogComponent: ComponentType<unknown> = DialogComponent
  ): Observable<boolean> {
    const dialogRef = this.matDialog.open<unknown, D, boolean>(dialogComponent, {
      data: dialogData,
      ...(basicDialogConfig satisfies MatDialogConfig<D>),
    });
    return dialogRef.afterClosed().pipe(
      take(1),
      map((value) => value || false)
    );
  }

  openDialogDataWithCustomResult<D extends DialogData, R>(
    dialogData: D,
    dialogComponent: ComponentType<unknown> = DialogComponent,
    dialogConfig?: Pick<MatDialogConfig<D>, 'width'>
  ): Observable<R | undefined> {
    const dialogRef = this.matDialog.open<unknown, D, R>(dialogComponent, {
      data: dialogData,
      ...(basicDialogConfig satisfies MatDialogConfig<D>),
      ...(dialogConfig satisfies MatDialogConfig<D> | undefined),
    });
    return dialogRef.afterClosed().pipe(take(1));
  }

  openCustomDataWithConfirmationResult<T, D>(
    data: D,
    dialogComponent: ComponentType<T>,
    dialogConfig?: Pick<MatDialogConfig<D>, 'width'>
  ): Observable<boolean> {
    const dialogRef = this.matDialog.open<T, D, boolean>(dialogComponent, {
      data,
      ...(basicDialogConfig satisfies MatDialogConfig<D>),
      ...(dialogConfig satisfies MatDialogConfig<D> | undefined),
    });
    return dialogRef.afterClosed().pipe(
      take(1),
      map((value) => value || false)
    );
  }

  openWithoutResult<T, D extends DialogData>(
    dialogComponent: ComponentType<T>,
    dialogData: D,
    dialogConfig?: Pick<MatDialogConfig<D>, 'minWidth'>
  ) {
    this.matDialog.open<T, D, undefined>(dialogComponent, {
      data: dialogData,
      ...(basicDialogConfig satisfies MatDialogConfig<D>),
      ...(dialogConfig satisfies MatDialogConfig<D> | undefined),
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
