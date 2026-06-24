import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'atlas-form-date-icon',
  templateUrl: './date-icon.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./date-icon.component.scss'],
})
export class DateIconComponent {
  readonly enabled = input.required<boolean>();
}
