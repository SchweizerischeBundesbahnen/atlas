import { Component, Input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'form-date-range',
  templateUrl: './date-range.component.html',
})
export class DateRangeComponent {
  @Input() formGroup!: FormGroup;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;
}
