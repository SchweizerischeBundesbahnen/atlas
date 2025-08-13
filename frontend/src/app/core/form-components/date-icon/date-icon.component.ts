import { Component, input } from '@angular/core';

@Component({
  selector: 'form-date-icon',
  templateUrl: './date-icon.component.html',
  styleUrls: ['./date-icon.component.scss'],
})
export class DateIconComponent {
  readonly enabled = input.required<boolean>();
}
