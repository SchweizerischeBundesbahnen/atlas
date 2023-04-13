import { Component, Input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'form-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['../text-field/text-field.component.scss'],
})
export class DateRangeComponent {
  @Input() formGroup!: FormGroup;
  @Input() labelFrom = 'COMMON.VALID_FROM';
  @Input() labelFromExample = '';
  @Input() labelUntil = 'COMMON.VALID_TO';
  @Input() labelUntilExample = '';
  @Input() infoIconTitleFrom = '';
  @Input() infoIconTitleUntil = '';
  @Input() required = true;
  @Input() setDateExamples = false;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';
}
