import {DialogData} from '../../../../../core/components/dialog/dialog.data';
import {StatementStatus, TimetableHearingStatementV2} from '../../../../../api';

export interface StatusChangeData extends DialogData {
  tths: TimetableHearingStatementV2[];
  statementStatus: StatementStatus;
  justification: string | undefined;
  type: StatusChangeDataType;
}

export type StatusChangeDataType = 'SINGLE' | 'MULTIPLE';
