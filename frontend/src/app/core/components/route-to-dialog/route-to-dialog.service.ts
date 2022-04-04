import { EventEmitter, Injectable } from '@angular/core';
import { DialogReference } from './dialog-reference';

@Injectable({
  providedIn: 'root',
})
export class RouteToDialogService {
  private lastDialogRef?: DialogReference;
  detailDialogEvent: EventEmitter<DetailDialogEvents> = new EventEmitter();

  hasDialog(): boolean {
    return !!this.lastDialogRef;
  }

  getDialog(): DialogReference {
    return this.lastDialogRef!;
  }

  closeDialog(): void {
    this.lastDialogRef?.close();
    this.detailDialogEvent.emit(DetailDialogEvents.Closed);
  }

  setDialogRef(dialogRef: DialogReference) {
    this.detailDialogEvent.emit(DetailDialogEvents.Opened);
    this.lastDialogRef = dialogRef;
  }

  clearDialogRef() {
    this.lastDialogRef = undefined;
  }
}

export enum DetailDialogEvents {
  Opened = 'opened',
  Closed = 'closed',
}
