import { DialogData } from '../../../../../core/components/dialog/dialog.data';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../../api';

export interface ChangeCantonData extends DialogData {
  tths: TimetableHearingStatementV2[];
  swissCanton: SwissCanton;
}
