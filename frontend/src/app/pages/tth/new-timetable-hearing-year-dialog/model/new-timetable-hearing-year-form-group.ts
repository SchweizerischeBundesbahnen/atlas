import { FormControl } from '@angular/forms';
import { Moment } from 'moment';

export interface NewTimetableHearingYearFormGroup {
  timetableYear: FormControl<number | null>;
  validFrom: FormControl<Moment | null>;
  validTo: FormControl<Moment | null>;
}
