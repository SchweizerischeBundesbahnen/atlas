import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TimetableHearingDialogService } from './timetable-hearing-dialog.service';

const timetableHearingDialogData = {
  title: 'Question',
};

describe('TimetableHearingDialogService', () => {
  let timetableHearingDialogService: TimetableHearingDialogService;

  const timetableHearingDialogSpy = jasmine.createSpyObj('timetableHearingDialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: timetableHearingDialogSpy }],
    });
    timetableHearingDialogService = TestBed.inject(TimetableHearingDialogService);
  });

  it('should open confirmation timetablehearingdialog and pass success value - true', () => {
    timetableHearingDialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    timetableHearingDialogService
      .confirm(timetableHearingDialogData)
      .subscribe((result) => expect(result).toBeTrue());

    expect(timetableHearingDialogSpy.open).toHaveBeenCalled();
  });

  it('should open confirmation timetablehearingdialog and pass cancel value - false', () => {
    timetableHearingDialogSpy.open.and.returnValue({ afterClosed: () => of(false) });

    timetableHearingDialogService
      .confirm(timetableHearingDialogData)
      .subscribe((result) => expect(result).toBeFalse());

    expect(timetableHearingDialogSpy.open).toHaveBeenCalled();
  });
});
