import { FormControl } from '@angular/forms';
import { WorkflowStatus } from '../../../../api';
import { LineDetailFormGroup } from '../../lines/detail/line-detail-form-group';

export interface LineVersionSnapshotDetailFormGroup
  extends LineDetailFormGroup {
  workflowId: FormControl<number | null>;
  workflowStatus: FormControl<WorkflowStatus | null>;
  parentObjectId: FormControl<number | null>;
}
