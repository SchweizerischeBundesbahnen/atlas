import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'atlas-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.scss'],
})
export class LinkComponent {

  @Input() text!: string;
  @Output() linkClicked = new EventEmitter<void>();

}
