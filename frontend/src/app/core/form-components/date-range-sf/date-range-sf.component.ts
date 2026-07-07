import { Component, input } from '@angular/core';
import {
  TodayAndFutureTimetableHeaderComponent
} from '../date-range/today-and-future-timetable-header/today-and-future-timetable-header.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { DateIconComponent } from '../date-icon/date-icon.component';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { Field, FormField } from '@angular/forms/signals';
import { Moment } from 'moment/moment';

@Component({
  selector: 'atlas-date-range-sf',
  imports: [
    AtlasLabelFieldComponent,
    DateIconComponent,
    MatDatepicker,
    AtlasFieldErrorSfComponent,
    MatDatepickerInput,
    FormField,
  ],
  templateUrl: './date-range-sf.component.html',
  styleUrls: ['../text-field/text-field.component.scss'],
})
export class DateRangeSfComponent {
  validFromHeader = TodayAndFutureTimetableHeaderComponent;

  readonly validFromField = input.required<Field<Moment | null>>();
  readonly validToField = input.required<Field<Moment | null>>();

  readonly labelFrom = input('COMMON.VALID_FROM');
  readonly labelFromExample = input('');
  readonly labelUntil = input('COMMON.VALID_TO');
  readonly labelUntilExample = input('');
  readonly infoIconTitleFrom = input('');
  readonly infoIconTitleUntil = input('');
  readonly required = input(true);
  readonly setDateExamples = input(false);
  readonly showMaxValidityAutoFill = input(true);

  readonly fieldNameFrom = input('validFrom');
  readonly fieldNameTo = input('validTo');

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';
}
