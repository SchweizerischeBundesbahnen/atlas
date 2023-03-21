import {
  StatementStatus,
  TimetableHearingStatementResponsibleTransportCompany,
} from '../../../api';

export interface TimeTableHearingStatementDisplay {
  statementStatus: StatementStatus | undefined;
  ttfnid?: string | undefined;
  timetableFieldNumber?: string | undefined;
  responsibleTransportCompanies?:
    | Array<TimetableHearingStatementResponsibleTransportCompany>
    | undefined;
  cantonDisplay: string | undefined;
  editorNameDisplay: string | Promise<string> | undefined;
  editionDate?: string | undefined;
}
