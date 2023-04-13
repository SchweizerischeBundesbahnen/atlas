import { FormControl } from '@angular/forms';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';

export interface NewTimetableHearingYearFormGroup extends BaseDetailFormGroup {
  timetableYear: FormControl<number | null>;
}
