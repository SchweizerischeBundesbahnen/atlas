import { Component, Input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'form-date-range',
  templateUrl: './date-range.component.html',
})
export class DateRangeComponent {
  @Input() formGroup!: UntypedFormGroup;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';

  get isEnabled(): boolean {
    return this.formGroup.enabled;
  }
}
