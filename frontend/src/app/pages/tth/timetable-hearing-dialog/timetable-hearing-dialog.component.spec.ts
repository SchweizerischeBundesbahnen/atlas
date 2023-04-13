import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../app.testing.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { TimetableHearingDialogComponent } from './timetable-hearing-dialog.component';
import {
  ContainerTimetableHearingYear,
  TimetableHearingService,
  TimetableHearingYear,
} from '../../../api';
import moment from 'moment/moment';
import { of } from 'rxjs';

const mockTimetableHearingService = jasmine.createSpyObj('timetableHearingService', [
  'getHearingYears',
]);

describe('TimetableHearingDialogComponent', () => {
  let timetableHearingDialogComponent: TimetableHearingDialogComponent;
  let fixture: ComponentFixture<TimetableHearingDialogComponent>;

  const getTimetableHearingYears = function (): TimetableHearingYear[] {
    const currentYear = getCurrentYear();
    const currentTTHY: TimetableHearingYear = {
      timetableYear: currentYear,
      hearingStatus: 'ACTIVE',
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    const nextTTHY: TimetableHearingYear = {
      timetableYear: currentYear + 1,
      hearingStatus: 'PLANNED',
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    const tthyIn2Years: TimetableHearingYear = {
      timetableYear: currentYear + 2,
      hearingStatus: 'PLANNED',
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    const tthIn3Years: TimetableHearingYear = {
      timetableYear: currentYear + 3,
      hearingStatus: 'PLANNED',
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    return [currentTTHY, nextTTHY, tthyIn2Years, tthIn3Years];
  };

  const hearingContainer: ContainerTimetableHearingYear = {
    objects: getTimetableHearingYears(),
    totalCount: 4,
  };

  function getCurrentYear() {
    return new Date().getUTCFullYear();
  }

  beforeEach(async () => {
    mockTimetableHearingService.getHearingYears.and.returnValue(of(hearingContainer));
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingDialogComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { title: 'Title' } },
        { provide: MatDialogRef, useValue: {} },
        { provide: TimetableHearingService, useValue: mockTimetableHearingService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimetableHearingDialogComponent);
    timetableHearingDialogComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create dialog with title, dropdown, formDateRange and matDialogActions', () => {
    expect(timetableHearingDialogComponent).toBeTruthy();

    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.innerText).toBe('Title');

    const dropdownLabel = fixture.debugElement.query(By.css('app-atlas-label-field'));
    expect(dropdownLabel.nativeElement).toBeTruthy();

    const dropdownSelect = fixture.debugElement.query(By.css('mat-select'));
    expect(dropdownSelect.nativeElement).toBeTruthy();

    const formDateRange = fixture.debugElement.query(By.css('form-date-range'));
    expect(formDateRange.nativeElement).toBeTruthy();

    const matDialogActions = fixture.debugElement.query(By.css('mat-dialog-actions'));
    expect(matDialogActions.nativeElement).toBeTruthy();
  });

  it('should get year options and default year selection', () => {
    const currentYear = getCurrentYear();
    timetableHearingDialogComponent.initOverviewOfferedYears();
    expect(timetableHearingDialogComponent.YEAR_OPTIONS).toEqual([
      currentYear + 4,
      currentYear + 5,
      currentYear + 6,
      currentYear + 7,
      currentYear + 8,
    ]);
    expect(timetableHearingDialogComponent.defaultYearSelection).toEqual(currentYear + 4);
  });

  it('should get active year', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(timetableHearingDialogComponent.getActiveYear(timetableHearingYears)).toEqual(
      getCurrentYear()
    );
  });

  it('should get planned years', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(timetableHearingDialogComponent.getPlannedYears(timetableHearingYears)).toEqual([
      timetableHearingYears[1],
      timetableHearingYears[2],
      timetableHearingYears[3],
    ]);
  });

  it('should check if year is already planned', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      timetableHearingDialogComponent.isYearAlreadyPlanned(
        getCurrentYear() + 1,
        timetableHearingYears
      )
    ).toBeTrue();
  });

  it('should check if year is not already planned', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      timetableHearingDialogComponent.isYearAlreadyPlanned(
        getCurrentYear() + 4,
        timetableHearingYears
      )
    ).toBeFalse();
  });

  it('should calculate proposed years', () => {
    const currentYear = getCurrentYear();
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      timetableHearingDialogComponent.calculateProposedYears(currentYear, timetableHearingYears)
    ).toEqual([
      currentYear + 4,
      currentYear + 5,
      currentYear + 6,
      currentYear + 7,
      currentYear + 8,
    ]);
  });
});
