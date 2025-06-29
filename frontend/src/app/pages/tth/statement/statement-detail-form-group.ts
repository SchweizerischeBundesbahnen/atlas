import { FormArray, FormControl, FormGroup } from '@angular/forms';
import {
  StatementStatus,
  SwissCanton,
  TimetableHearingStatementResponsibleTransportCompany,
} from '../../../api';

export interface StatementDetailFormGroup {
  id: FormControl<number | null | undefined>;
  timetableYear: FormControl<number | null | undefined>;
  statementStatus: FormControl<StatementStatus | null | undefined>;
  ttfnid: FormControl<string | null | undefined>;
  responsibleTransportCompanies: FormControl<
    | Array<TimetableHearingStatementResponsibleTransportCompany>
    | null
    | undefined
  >;
  swissCanton: FormControl<SwissCanton | null | undefined>;
  oldSwissCanton: FormControl<SwissCanton | null | undefined>;
  stopPlace: FormControl<string | null | undefined>;
  statement: FormControl<string | null | undefined>;
  statementSender: FormGroup<StatementSenderFormGroup>;
  justification: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
  documents: FormArray;
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
