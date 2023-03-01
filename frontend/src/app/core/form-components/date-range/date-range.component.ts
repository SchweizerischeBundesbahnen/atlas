import { ChangeDetectorRef, Component, Input, ViewChild } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';

@Component({
  selector: 'form-date-range',
  templateUrl: './date-range.component.html',
})
export class DateRangeComponent {
  @Input() formGroup!: FormGroup;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';
  @ViewChild('validFromPicker') validFromPicker!: MatDatepicker<Date>;
  @ViewChild('validToPicker') validToPicker!: MatDatepicker<Date>;

  constructor(private cdref: ChangeDetectorRef) {}

  ngAfterContentChecked() {
    this.cdref.detectChanges();
  }
}
