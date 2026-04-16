import { FormArray, FormControl, FormGroup } from '@angular/forms';
import {
  StatementStatus,
  SwissCanton,
  TimetableHearingStatementDocument,
  TimetableHearingStatementResponsibleTransportCompany,
} from '../../../../api';

export interface StatementDetailFormGroup {
  id: FormControl<number | null | undefined>;
  timetableYear: FormControl<number | null | undefined>;
  statementStatus: FormControl<StatementStatus | null | undefined>;
  ttfnid: FormControl<string | null | undefined>;
  responsibleTransportCompanies: FormControl<
    Array<TimetableHearingStatementResponsibleTransportCompany> | null | undefined
  >;
  swissCanton: FormControl<SwissCanton | null | undefined>;
  oldSwissCanton: FormControl<SwissCanton | null | undefined>;
  stopPlace: FormControl<string | null | undefined>;
  statement: FormControl<string | null | undefined>;
  anonymousStatement: FormControl<string | null | undefined>;
  statementAnonymous: FormControl<boolean | null | undefined>;
  statementSender: FormGroup<StatementSenderFormGroup>;
  publicComment: FormControl<string | null | undefined>;
  internalComment: FormControl<string | null | undefined>;
  cantonTransferComment: FormControl<string | null | undefined>;
  topic: FormControl<string | null | undefined>;
  documents: FormArray<FormGroup<TimetableHearingStatementDocumentGroup>>;
  etagVersion: FormControl<number | null | undefined>;
  editor: FormControl<string | null | undefined>;
}

export interface StatementSenderFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  zip: FormControl<number | null | undefined>;
  city: FormControl<string | null | undefined>;
  street: FormControl<string | null | undefined>;
  emails: FormControl<Array<string> | null | undefined>;
}

export interface TimetableHearingStatementDocumentGroup {
  id: FormControl<number | null | undefined>;
  fileName: FormControl<string | null>;
  fileSize: FormControl<number | null>;
  anonymous: FormControl<boolean | null | undefined>;
}

export class TimetableHearingStatementBuilder {
  static buildTimetableHearingStatementDocumentGroup(
    timetableHearingStatementDocument: TimetableHearingStatementDocument
  ): FormGroup<TimetableHearingStatementDocumentGroup> {
    return new FormGroup<TimetableHearingStatementDocumentGroup>({
      id: new FormControl(timetableHearingStatementDocument.id),
      anonymous: new FormControl(timetableHearingStatementDocument.anonymous),
      fileName: new FormControl(timetableHearingStatementDocument.fileName),
      fileSize: new FormControl(timetableHearingStatementDocument.fileSize),
    });
  }
}
