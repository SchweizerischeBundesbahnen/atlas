import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogComponent } from './dialog.component';
import { DialogData } from './dialog.data';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  constructor(private dialog: MatDialog) {}

  confirm(dialogData: DialogData): Observable<boolean> {
    const dialogComponent = this.dialog.open(DialogComponent, { data: dialogData });
    return dialogComponent.afterClosed().pipe(map((value) => (value ? value : false)));
  }
}
