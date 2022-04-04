import { Injectable } from '@angular/core';
import { DialogReference } from './dialog-reference';

@Injectable({
  providedIn: 'root',
})
export class RouteToDialogService {
  private lastDialogRef?: DialogReference;

  hasDialog(): boolean {
    return !!this.lastDialogRef;
  }

  getDialog(): DialogReference {
    return this.lastDialogRef!;
  }

  closeDialog(): void {
    this.lastDialogRef?.close();
  }

  setDialogRef(dialogRef: DialogReference) {
    this.lastDialogRef = dialogRef;
  }

  clearDialogRef() {
    this.lastDialogRef = undefined;
  }
}
