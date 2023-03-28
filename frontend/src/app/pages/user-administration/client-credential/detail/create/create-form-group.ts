import { FormControl } from '@angular/forms';

export interface ClientCredentialCreateFormGroup {
  clientCredentialId: FormControl<string | null | undefined>;
  alias: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
}
