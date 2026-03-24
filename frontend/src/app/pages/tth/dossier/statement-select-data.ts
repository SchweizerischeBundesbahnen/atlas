import { SwissCanton } from '../../../api';
import { DialogData } from '../../../core/components/dialog/dialog.data';

export interface StatementSelectData extends DialogData {
  selectedStatements: number[];
  swissCanton: SwissCanton;
  timetableHearingYear: number;
}
