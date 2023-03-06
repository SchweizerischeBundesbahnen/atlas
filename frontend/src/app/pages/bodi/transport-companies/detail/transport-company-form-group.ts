import { FormControl } from '@angular/forms';

export interface TransportCompanyFormGroup {
  id: FormControl<number | null | undefined>;
  number: FormControl<string | null | undefined>;
  abbreviation: FormControl<string | null | undefined>;
  description: FormControl<string | null | undefined>;
  enterpriseId: FormControl<string | null | undefined>;
  businessRegisterName: FormControl<string | null | undefined>;
  businessRegisterNumber: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
}
