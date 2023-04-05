import { FormControl, FormGroup } from '@angular/forms';
import {
  StatementStatus,
  SwissCanton,
  TimetableHearingStatementResponsibleTransportCompany,
} from '../../../api';

export interface StatementDetailFormGroup {
  timetableYear: FormControl<number | null | undefined>;
  statementStatus: FormControl<StatementStatus | null | undefined>;
  ttfnid: FormControl<string | null | undefined>;
  responsibleTransportCompanies: FormControl<
    Array<TimetableHearingStatementResponsibleTransportCompany> | null | undefined
  >;
  swissCanton: FormControl<SwissCanton | null | undefined>;
  stopPlace: FormControl<string | null | undefined>;
  statement: FormControl<string | null | undefined>;
  statementSender: FormGroup<StatementSenderFormGroup>;
  justification: FormControl<string | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
}

export interface StatementSenderFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  zip: FormControl<number | null | undefined>;
  city: FormControl<string | null | undefined>;
  street: FormControl<string | null | undefined>;
  email: FormControl<string | null | undefined>;
}
