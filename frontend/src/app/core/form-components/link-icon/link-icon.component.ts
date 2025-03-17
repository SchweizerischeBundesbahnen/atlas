import { Component, Input } from '@angular/core';

@Component({
    selector: 'link-icon',
    templateUrl: './link-icon.component.html',
    styleUrls: ['./link-icon.component.scss'],
    standalone: false
})
export class LinkIconComponent {
  @Input() enabled!: boolean;
}
