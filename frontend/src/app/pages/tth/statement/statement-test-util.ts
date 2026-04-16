import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  SwissCanton,
  TimetableHearingStatementResponsibleTransportCompany,
  TimetableHearingStatementV2,
} from '../../../api';

const responsibleTransportCompanies: Array<TimetableHearingStatementResponsibleTransportCompany> = [
  {
    id: 1,
    number: '123456',
    abbreviation: 'sbb',
    businessRegisterName: 'sbb-123',
  },
];

const statementSender = new FormGroup({
  city: new FormControl(),
  firstName: new FormControl(),
  lastName: new FormControl(),
  organisation: new FormControl(),
  street: new FormControl(),
  zip: new FormControl(),
  emails: new FormControl(['a@a.ch']),
});

export const statementFormGroup = new FormGroup({
  statementSender: statementSender,
  documents: new FormBuilder().array([]),
  topic: new FormControl('Cosa vuoi'),
  responsibleTransportCompanies: new FormControl(responsibleTransportCompanies ?? []),
  stopPlace: new FormControl('Bern'),
});

export const statement: TimetableHearingStatementV2 = {
  id: 1234,
  topic: 'Cosa voui',
  swissCanton: SwissCanton.Aargau,
  statement: 'Mehr Busse bitte',
  statementSender: {
    emails: new Set(['fan@yb.ch', 'fan@nap.ch']),
  },
  documents: [
    {
      id: 1,
      anonymous: true,
      fileName: 'file1',
      fileSize: 12,
    },
    {
      id: 2,
      anonymous: false,
      fileName: 'file1',
      fileSize: 12,
    },
  ],
};
