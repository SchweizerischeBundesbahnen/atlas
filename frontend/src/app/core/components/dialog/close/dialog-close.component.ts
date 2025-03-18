import { Component, EventEmitter, Output } from '@angular/core';

@Component({
    selector: 'atlas-dialog-close',
    templateUrl: './dialog-close.component.html',
    styleUrls: ['./dialog-close.component.scss']
})
export class DialogCloseComponent {
  @Output() clicked: EventEmitter<void> = new EventEmitter<void>();
}
