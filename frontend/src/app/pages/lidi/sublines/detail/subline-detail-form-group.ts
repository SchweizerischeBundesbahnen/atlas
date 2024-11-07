import {BaseDetailFormGroup} from '../../../../core/components/base-detail/base-detail-form-group';
import {FormControl} from '@angular/forms';
import {PaymentType, SublineType} from '../../../../api';

export interface SublineDetailFormGroup extends BaseDetailFormGroup {
  swissSublineNumber: FormControl<string | null>;
  mainlineSlnid: FormControl<string | null>;
  slnid: FormControl<string | null | undefined>;
  status: FormControl<string | null | undefined>;
  sublineType: FormControl<SublineType | null>;
  paymentType: FormControl<PaymentType | null | undefined>;
  businessOrganisation: FormControl<string | null>;
  number: FormControl<string | null | undefined>;
  longName: FormControl<string | null | undefined>;
  description: FormControl<string | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
}
