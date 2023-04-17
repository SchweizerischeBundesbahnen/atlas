import { FormControl } from '@angular/forms';
import { Moment } from 'moment/moment';

export interface NewTimetableHearingYearFormGroup {
  timetableYear: FormControl<number | null>;
  hearingFrom: FormControl<Moment | null>;
  hearingTo: FormControl<Moment | null>;
}
