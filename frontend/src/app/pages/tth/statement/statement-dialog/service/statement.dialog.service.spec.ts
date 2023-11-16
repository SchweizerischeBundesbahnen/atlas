import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { SwissCanton } from '../../../../../api';
import { StatementDialogService } from './statement.dialog.service';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  StatementDetailFormGroup,
  StatementSenderFormGroup,
} from '../../statement-detail-form-group';

const form = new FormGroup<StatementDetailFormGroup>({
  id: new FormControl(),
  timetableYear: new FormControl(),
  statementStatus: new FormControl(),
  ttfnid: new FormControl(),
  responsibleTransportCompanies: new FormControl(),
  swissCanton: new FormControl(SwissCanton.Bern),
  statementSender: new FormGroup<StatementSenderFormGroup>({
    firstName: new FormControl(),
    lastName: new FormControl(),
    organisation: new FormControl(),
    zip: new FormControl(),
    city: new FormControl(),
    street: new FormControl(),
    email: new FormControl(),
  }),
  stopPlace: new FormControl(),
  statement: new FormControl(),
  justification: new FormControl(),
  comment: new FormControl(),
  documents: new FormBuilder().array([]),
  etagVersion: new FormControl(),
});

describe('StatementDialogService', () => {
  let service: StatementDialogService;

  const dialogSpy = jasmine.createSpyObj('statementDialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(StatementDialogService);
  });

  it('should open statement comment dialog and pass cancel value - true', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service.openDialog(form).subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });

  it('should open statement comment dialog and pass cancel value - false', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(false) });

    service.openDialog(form).subscribe((result) => expect(result).toBeFalse());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
