import { DialogData } from '../../../../../core/components/dialog/dialog.data';
import { SwissCanton, TimetableHearingStatement } from '../../../../../api';

export interface ChangeCantonData extends DialogData {
  tths: TimetableHearingStatement[];
  swissCanton: SwissCanton;
}
