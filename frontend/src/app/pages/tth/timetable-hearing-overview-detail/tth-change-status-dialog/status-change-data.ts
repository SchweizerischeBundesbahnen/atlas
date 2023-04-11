import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { TimetableHearingStatement } from '../../../../api';

export interface StatusChangeData extends DialogData {
  id: number;
  ths: TimetableHearingStatement;
}
