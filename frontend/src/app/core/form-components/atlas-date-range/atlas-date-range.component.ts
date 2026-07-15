import { Component, input } from '@angular/core';
import { TodayAndFutureTimetableHeaderComponent } from '../date-range/today-and-future-timetable-header/today-and-future-timetable-header.component';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { Field, FormField } from '@angular/forms/signals';
import { Moment } from 'moment/moment';
import { AtlasFieldErrorComponent } from '@atlas/form/lib/atlas-field-error/atlas-field-error.component';
import { AtlasDateIconComponent } from '@atlas/form/lib/atlas-date-icon/atlas-date-icon.component';
import { AtlasLabelFieldComponent } from '@atlas/form/lib/atlas-label-field/atlas-label-field.component';

@Component({
  selector: 'atlas-date-range',
  imports: [
    AtlasLabelFieldComponent,
    AtlasDateIconComponent,
    AtlasFieldErrorComponent,
    MatDatepicker,
    MatDatepickerInput,
    FormField,
  ],
  templateUrl: './atlas-date-range.component.html',
  styleUrls: ['../../../../../projects/form/src/lib/atlas-text-field/atlas-text-field.component.scss'],
})
export class AtlasDateRangeComponent {
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
