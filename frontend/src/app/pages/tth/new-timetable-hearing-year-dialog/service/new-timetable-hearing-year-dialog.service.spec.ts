import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { NewTimetableHearingYearDialogService } from './new-timetable-hearing-year-dialog.service';

const timetableHearingDialogData = {
  title: 'Question',
};

describe('NewTimetableHearingYearDialogService', () => {
  let newTimetableHearingYearDialogService: NewTimetableHearingYearDialogService;

  const timetableHearingDialogSpy = jasmine.createSpyObj('newTimetableHearingYearDialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: timetableHearingDialogSpy }],
    });
    newTimetableHearingYearDialogService = TestBed.inject(NewTimetableHearingYearDialogService);
  });

  it('should open confirmation new timetable hearing year dialog and pass success value - true', () => {
    timetableHearingDialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    newTimetableHearingYearDialogService
      .confirm(timetableHearingDialogData)
      .subscribe((result) => expect(result).toBeTrue());

    expect(timetableHearingDialogSpy.open).toHaveBeenCalled();
  });

  it('should open confirmation new timetable hearing year dialog and pass cancel value - false', () => {
    timetableHearingDialogSpy.open.and.returnValue({ afterClosed: () => of(false) });

    newTimetableHearingYearDialogService
      .confirm(timetableHearingDialogData)
      .subscribe((result) => expect(result).toBeFalse());

    expect(timetableHearingDialogSpy.open).toHaveBeenCalled();
  });
});
