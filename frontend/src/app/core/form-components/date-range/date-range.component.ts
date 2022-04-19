import { Component, Input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { AbstractControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'form-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['./date-range.component.scss'],
})
export class DateRangeComponent {
  @Input() formGroup!: FormGroup;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';

  isEnabled(fromName: string): boolean {
    return this.getFormControl(fromName).enabled;
  }

  getFormControl(formName: string): AbstractControl {
    return <AbstractControl>this.formGroup.get([formName]);
  }

}
