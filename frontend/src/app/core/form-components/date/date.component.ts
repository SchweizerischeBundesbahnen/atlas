import { Component, input, Input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DateIconComponent } from '../date-icon/date-icon.component';

@Component({
  selector: 'atlas-form-date',
  templateUrl: './date.component.html',
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
export class DateComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly label = input('COMMON.VALID_FROM');
  readonly labelExample = input('');
  readonly labelUntil = input('COMMON.VALID_TO');
  readonly labelUntilExample = input('');
  readonly infoIconTitle = input('');
  readonly required = input(true);
  readonly setDateExamples = input(false);

  readonly controlName = input('validFrom');
  readonly controlNameTo = input('validTo');
  readonly readonly = input(false);

  readonly minDate = input(MIN_DATE);
  readonly maxDate = input(MAX_DATE);

  readonly EXAMPLE_DATE = '21.01.2021';

  get controlFrom() {
    return this.formGroup.get(this.controlName())!;
  }
}
