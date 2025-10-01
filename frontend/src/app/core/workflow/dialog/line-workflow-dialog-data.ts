import { DialogData } from '../../components/dialog/dialog.data';
import { LineRecord } from '../model/line-record';

export interface LineWorkflowDialogData extends DialogData {
  lineRecord: LineRecord;
  descriptionForWorkflow: string;
  number?: string;
}
