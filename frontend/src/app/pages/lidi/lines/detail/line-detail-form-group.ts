import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import { FormControl } from '@angular/forms';
import { LineType, PaymentType } from '../../../../api';

export interface LineDetailFormGroup extends BaseDetailFormGroup {
  swissLineNumber: FormControl<string | null>;
  lineType: FormControl<LineType | null>;
  paymentType: FormControl<PaymentType | null>;
  businessOrganisation: FormControl<string | null>;
  number: FormControl<string | null | undefined>;
  alternativeName: FormControl<string | null | undefined>;
  combinationName: FormControl<string | null | undefined>;
  longName: FormControl<string | null | undefined>;
  icon: FormControl<string | null | undefined>;
  colorFontRgb: FormControl<string | null>;
  colorBackRgb: FormControl<string | null>;
  colorFontCmyk: FormControl<string | null>;
  colorBackCmyk: FormControl<string | null>;
  description: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
}
