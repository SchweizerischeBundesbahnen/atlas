import { FormControl } from '@angular/forms';
import { WorkflowCheckFormGroup } from './workflow-check-form/workflow-check-form-group';

export interface WorkflowFormGroup extends WorkflowCheckFormGroup {
  mail: FormControl<string | null | undefined>;
}
