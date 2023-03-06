import { FormControl } from '@angular/forms';

export interface CompanyFormGroup {
  uicCode: FormControl<number | null | undefined>;
  countryCodeIso: FormControl<string | null | undefined>;
  shortName: FormControl<string | null | undefined>;
  name: FormControl<string | null | undefined>;
  url: FormControl<string | null | undefined>;
}
