import { BaseDetailFormGroup } from '../../../core/components/base-detail/base-detail-form-group';
import { FormControl } from '@angular/forms';
import { Status } from '../../../api';

export interface TimetableFieldNumberDetailFormGroup
  extends BaseDetailFormGroup {
  swissTimetableFieldNumber: FormControl<string | null>;
  ttfnid: FormControl<string | null | undefined>;
  businessOrganisation: FormControl<string | null>;
  number: FormControl<string | null>;
  description: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
  status: FormControl<Status | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
}
