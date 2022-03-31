import {Injectable} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';

@Injectable({
  providedIn: 'root',
})
export class RouteToDialogService {
  private lastDialogRef?: MatDialogRef<any>;

  hasDialog(): boolean {
    return !!this.lastDialogRef;
  }

  getDialog(): MatDialogRef<any> {
    return this.lastDialogRef!;
  }

  closeDialog(): void {
    this.lastDialogRef?.close();
  }

  setDialogRef(dialogRef: MatDialogRef<any>) {
    this.lastDialogRef = dialogRef;
  }

  clearDialogRer() {
    this.lastDialogRef = undefined;
  }
}
