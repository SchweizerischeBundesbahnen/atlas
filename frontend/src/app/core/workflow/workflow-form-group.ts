import { FormControl } from '@angular/forms';

export interface WorkflowFormGroup {
  comment: FormControl<string | null | undefined>;
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  function: FormControl<string | null | undefined>;
  mail: FormControl<string | null | undefined>;
}
