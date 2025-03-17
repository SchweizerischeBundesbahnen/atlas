import { Component, EventEmitter, Output } from '@angular/core';

@Component({
    selector: 'atlas-dialog-close',
    templateUrl: './dialog-close.component.html',
    styleUrls: ['./dialog-close.component.scss'],
    standalone: false
})
export class DialogCloseComponent {
  @Output() clicked: EventEmitter<void> = new EventEmitter<void>();
}
