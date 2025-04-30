import { Component, Input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TodayAndFutureTimetableHeaderComponent } from './today-and-future-timetable-header/today-and-future-timetable-header.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import {
  MatDatepickerInput,
  MatDatepickerToggle,
  MatDatepickerToggleIcon,
  MatDatepicker,
} from '@angular/material/datepicker';
import { MatIcon } from '@angular/material/icon';
import { DateIconComponent } from '../date-icon/date-icon.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'form-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['../text-field/text-field.component.scss'],
  imports: [
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatIcon,
    MatDatepickerToggleIcon,
    DateIconComponent,
    MatDatepicker,
    AtlasFieldErrorComponent,
  ],
})
export class DateRangeComponent {
  validFromHeader = TodayAndFutureTimetableHeaderComponent;

  @Input() formGroup!: FormGroup;
  @Input() labelFrom = 'COMMON.VALID_FROM';
  @Input() labelFromExample = '';
  @Input() labelUntil = 'COMMON.VALID_TO';
  @Input() labelUntilExample = '';
  @Input() infoIconTitleFrom = '';
  @Input() infoIconTitleUntil = '';
  @Input() required = true;
  @Input() setDateExamples = false;
  @Input() showMaxValidityAutoFill = true;

  @Input() controlNameFrom = 'validFrom';
  @Input() controlNameTo = 'validTo';

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';

  get controlFrom() {
    return this.formGroup.get(this.controlNameFrom)!;
  }

  get controlTo() {
    return this.formGroup.get(this.controlNameTo)!;
  }
}
