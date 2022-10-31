import { FormControl } from '@angular/forms';

export interface WorkflowFormGroup {
  comment: FormControl<any>;
  firstName: FormControl<any>;
  lastName: FormControl<any>;
  function: FormControl<any>;
  mail: FormControl<any>;
}
