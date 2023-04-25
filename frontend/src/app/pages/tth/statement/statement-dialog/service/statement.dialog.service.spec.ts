import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { SwissCanton, TimetableHearingStatement } from '../../../../../api';
import { StatementDialogService } from './statement.dialog.service';
import { FormGroup } from '@angular/forms';

const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'I would like to change timetable for Bus number 333.',
  justification: 'Current timetable is not good.',
  comment: 'I am changing statement canton.',
  statementSender: {
    email: 'atlas@sbb.ch',
  },
};

const form: FormGroup = statement as unknown as FormGroup;

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
