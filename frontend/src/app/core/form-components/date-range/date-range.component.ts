import { Component, Input, input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TodayAndFutureTimetableHeaderComponent } from './today-and-future-timetable-header/today-and-future-timetable-header.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DateIconComponent } from '../date-icon/date-icon.component';

@Component({
  selector: 'atlas-form-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['../text-field/text-field.component.scss'],
  imports: [
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    MatDatepickerInput,
    MatDatepicker,
    AtlasFieldErrorComponent,
    DateIconComponent,
  ],
  providers: [TranslatePipe],
})
export class DateRangeComponent {
  validFromHeader = TodayAndFutureTimetableHeaderComponent;

  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly labelFrom = input('COMMON.VALID_FROM');
  readonly labelFromExample = input('');
  readonly labelUntil = input('COMMON.VALID_TO');
  readonly labelUntilExample = input('');
  readonly infoIconTitleFrom = input('');
  readonly infoIconTitleUntil = input('');
  readonly required = input(true);
  readonly setDateExamples = input(false);
  readonly showMaxValidityAutoFill = input(true);

  readonly controlNameFrom = input('validFrom');
  readonly controlNameTo = input('validTo');

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly EXAMPLE_DATE_FROM = '21.01.2021';
  readonly EXAMPLE_DATE_TO = '31.12.9999';

  get controlFrom() {
    return this.formGroup.get(this.controlNameFrom())!;
  }

  get controlTo() {
    return this.formGroup.get(this.controlNameTo())!;
  }
}
