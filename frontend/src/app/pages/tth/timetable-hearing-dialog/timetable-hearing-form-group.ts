import { FormControl } from '@angular/forms';
import { Moment } from 'moment';

export interface TimetableHearingFormGroup {
  timetableYear: FormControl<number | null>;
  validFrom: FormControl<Moment | null>;
  validTo: FormControl<Moment | null>;
}
