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
  @Input() formGroup!: FormGroup;
  @Input() label = 'COMMON.VALID_FROM';
  @Input() labelExample = '';
  @Input() labelUntil = 'COMMON.VALID_TO';
  @Input() labelUntilExample = '';
  @Input() infoIconTitle = '';
  @Input() required = true;
  @Input() setDateExamples = false;

  @Input() controlName = 'validFrom';
  @Input() controlNameTo = 'validTo';
  @Input() readonly = false;

  minDate = input(MIN_DATE);
  maxDate = input(MAX_DATE);

  readonly EXAMPLE_DATE = '21.01.2021';

  get controlFrom() {
    return this.formGroup.get(this.controlName)!;
  }
}
