import {Component, Input, ViewChild} from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup } from '@angular/forms';
import {MatDatepicker} from "@angular/material/datepicker";
import moment, { Moment } from "moment/moment";
import {TimetableYearChangeService} from "../../../api";

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

  @Input() controlNameFrom = 'validFrom';
  @Input() controlNameTo = 'validTo';

  @ViewChild('validFromPicker') validFromPicker!: MatDatepicker<Moment>;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  constructor(private timetableYearChangeService: TimetableYearChangeService) {
  }

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';

  get controlFrom() {
    return this.formGroup.get(this.controlNameFrom)!;
  }

  get controlTo() {
    return this.formGroup.get(this.controlNameTo)!;
  }

  selectToday() {
    this.validFromPicker.select(moment());
    this.validFromPicker.close();
  }

  selectFutureTimetable() {
    this.timetableYearChangeService.getNextTimetablesYearChange(1).subscribe(dates => {
      this.validFromPicker.select(moment(dates[0]));
      this.validFromPicker.close();
    })
  }
}
