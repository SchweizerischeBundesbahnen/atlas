import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { ReadServicePointVersion } from '../../../../api';

export interface AddStopPointWorkflowDialogData extends DialogData {
  stopPoint: ReadServicePointVersion;
}
