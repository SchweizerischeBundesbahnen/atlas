import { DialogData } from '../../../../../../core/components/dialog/dialog.data';

export interface StopPointTerminationDialogData extends DialogData {
  versionId?: number;
  sloid?: string;
  boTerminationDate: Date;
}
