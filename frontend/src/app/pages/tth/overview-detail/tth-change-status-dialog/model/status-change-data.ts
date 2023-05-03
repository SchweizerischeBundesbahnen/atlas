import { DialogData } from '../../../../../core/components/dialog/dialog.data';
import { StatementStatus, TimetableHearingStatement } from '../../../../../api';

export interface StatusChangeData extends DialogData {
  tths: TimetableHearingStatement[];
  statementStatus: StatementStatus;
  justification?: string | undefined;
  type: StatusChangeDataType;
}

export type StatusChangeDataType = 'SINGLE' | 'MULTIPLE';
