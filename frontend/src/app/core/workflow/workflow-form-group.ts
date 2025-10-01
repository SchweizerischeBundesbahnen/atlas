import { FormControl } from '@angular/forms';
import { LineWorkflowCheckFormGroup } from './workflow-check-form/line-workflow-check-form-group';

export interface WorkflowFormGroup extends LineWorkflowCheckFormGroup {
  mail: FormControl<string | null | undefined>;
}
