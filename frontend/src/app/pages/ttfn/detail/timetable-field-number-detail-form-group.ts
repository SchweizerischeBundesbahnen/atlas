import { BaseDetailFormGroup } from '../../../core/components/base-detail/base-detail-form-group';
import { FormControl } from '@angular/forms';
import { MeanOfTransport, Status } from '../../../api';

export interface TimetableFieldNumberDetailFormGroup
  extends BaseDetailFormGroup {
  swissTimetableFieldNumber: FormControl<string | null>;
  ttfnid: FormControl<string | null | undefined>;
  businessOrganisation: FormControl<string | null>;
  number: FormControl<string | null>;
  status: FormControl<Status | null | undefined>;
  descriptionOutwardLine1: FormControl<string | undefined>;
  descriptionOutwardLine2: FormControl<string | null | undefined>;
  descriptionOutwardLine3: FormControl<string | null | undefined>;
  descriptionReturnLine1: FormControl<string | undefined>;
  descriptionReturnLine2: FormControl<string | null | undefined>;
  descriptionReturnLine3: FormControl<string | null | undefined>;
  meanOfTransport: FormControl<MeanOfTransport | undefined>;
}
