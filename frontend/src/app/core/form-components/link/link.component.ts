import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'atlas-link',
    templateUrl: './link.component.html',
    styleUrls: ['./link.component.scss'],
    standalone: false
})
export class LinkComponent {

  @Input() label!: string;
  @Output() linkClicked = new EventEmitter<void>();

}
