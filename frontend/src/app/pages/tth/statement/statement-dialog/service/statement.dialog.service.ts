import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { StatementDialogComponent } from '../statement.dialog.component';
import { FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class StatementDialogService {
  private dialogRef?: MatDialogRef<StatementDialogComponent>;

  constructor(private statementDialog: MatDialog) {}

  openDialog(form: FormGroup): Observable<boolean> {
    this.dialogRef = this.statementDialog.open(StatementDialogComponent, {
      data: form,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    return this.dialogRef.afterClosed().pipe(map((value) => (value ? value : false)));
  }
}
