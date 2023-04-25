import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { SwissCanton } from '../../../../../api';
import { StatementDialogService } from './statement.dialog.service';
import { FormControl, FormGroup } from '@angular/forms';

const form = new FormGroup({
  swissCanton: new FormControl(SwissCanton.Bern),
  comment: new FormControl('Changing canton.'),
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
