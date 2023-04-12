import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { TthChangeStatusDialogService } from './tth-change-status-dialog.service';
import { ColumnDropDownEvent } from '../../../../../core/components/table/column-drop-down-event';
import { SwissCanton, TimetableHearingStatement } from '../../../../../api';

const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  justification: 'Napoli ist besser als YB',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

const changedStatus: ColumnDropDownEvent = {
  $event: {
    value: statement,
  },
  value: statement,
};

describe('TthChangeStatusDialogService', () => {
  let service: TthChangeStatusDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(TthChangeStatusDialogService);
  });

  it('should open confirmation dialog', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service.onClick(changedStatus).subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
