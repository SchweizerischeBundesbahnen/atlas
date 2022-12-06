import { FormControl } from '@angular/forms';

export interface WorkflowCheckFormGroup {
  comment: FormControl<string | null | undefined>;
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  function: FormControl<string | null | undefined>;
}
