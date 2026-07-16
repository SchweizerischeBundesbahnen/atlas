import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasDateIconComponent, AtlasLabelFieldComponent } from '@atlas/form';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-form-date',
  templateUrl: './date.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['../text-field/text-field.component.scss'],
  imports: [
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    MatDatepickerInput,
    MatDatepicker,
    AtlasFieldErrorComponent,
    AtlasDateIconComponent,
  ],
  providers: [TranslatePipe],
})
export class DateComponent {
  readonly formGroup = input.required<FormGroup>();
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
    return this.formGroup().get(this.controlName())!;
  }
}
