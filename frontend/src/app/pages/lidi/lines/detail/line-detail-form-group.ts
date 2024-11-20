import {BaseDetailFormGroup} from '../../../../core/components/base-detail/base-detail-form-group';
import {FormControl} from '@angular/forms';
import {LineConcessionType, LineType, OfferCategory} from '../../../../api';

export interface LineDetailFormGroup extends BaseDetailFormGroup {
  swissLineNumber: FormControl<string | null>;
  lineType: FormControl<LineType | null>;
  offerCategory: FormControl<OfferCategory | null | undefined>;
  businessOrganisation: FormControl<string | null>;
  number: FormControl<string | null | undefined>;
  shortNumber: FormControl<string | null | undefined>;
  lineConcessionType: FormControl<LineConcessionType | null>;
  longName: FormControl<string | null | undefined>;
  description: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
}
