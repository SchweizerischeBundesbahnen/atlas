import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
    selector: 'form-date-icon',
    templateUrl: './date-icon.component.html',
    styleUrls: ['./date-icon.component.scss'],
    imports: [NgClass]
})
export class DateIconComponent {
  @Input() enabled!: boolean;
}
