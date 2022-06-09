import { FormControl } from '@angular/forms';
import { Moment } from 'moment';

export interface BaseDetailFormGroup {
  validFrom: FormControl<Moment | null>;
  validTo: FormControl<Moment | null>;
}
