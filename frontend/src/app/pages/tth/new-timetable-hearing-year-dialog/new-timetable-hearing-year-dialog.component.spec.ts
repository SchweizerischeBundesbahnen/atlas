import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../app.testing.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { NewTimetableHearingYearDialogComponent } from './new-timetable-hearing-year-dialog.component';
import { TimetableHearingYear, TimetableHearingYearsService } from '../../../api';
import moment from 'moment/moment';
import { of } from 'rxjs';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { MockAtlasFieldErrorComponent } from '../../../app.testing.mocks';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { DateRangeComponent } from '../../../core/form-components/date-range/date-range.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { DateIconComponent } from '../../../core/form-components/date-icon/date-icon.component';
import { TranslatePipe } from '@ngx-translate/core';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';

const mockTimetableHearingYearsService = jasmine.createSpyObj('timetableHearingService', [
  'getHearingYears',
]);

describe('NewTimetableHearingYearDialogComponent', () => {
  let newTimetableHearingYearDialogComponent: NewTimetableHearingYearDialogComponent;
  let fixture: ComponentFixture<NewTimetableHearingYearDialogComponent>;

  const currentYear = getCurrentYear();

  const getTimetableHearingYears = function (): TimetableHearingYear[] {
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

  function getCurrentYear() {
    return new Date().getUTCFullYear();
  }

  beforeEach(async () => {
    mockTimetableHearingYearsService.getHearingYears.and.returnValue(
      of(getTimetableHearingYears()),
    );
    await TestBed.configureTestingModule({
      declarations: [
        NewTimetableHearingYearDialogComponent,
        DateRangeComponent,
        DateIconComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        InfoIconComponent,
        SelectComponent,
        AtlasSpacerComponent,
        MockAtlasFieldErrorComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { title: 'Title' } },
        { provide: MatDialogRef, useValue: {} },
        { provide: TimetableHearingYearsService, useValue: mockTimetableHearingYearsService },
        TranslatePipe,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewTimetableHearingYearDialogComponent);
    newTimetableHearingYearDialogComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create dialog with title, dropdown, formDateRange and matDialogActions', () => {
    expect(newTimetableHearingYearDialogComponent).toBeTruthy();

    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.innerText).toBe('TTH.NEW_YEAR.DIALOG.NEW_PLAN_TIMETABLE');

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
    newTimetableHearingYearDialogComponent.initOverviewOfferedYears();
    expect(newTimetableHearingYearDialogComponent.YEAR_OPTIONS).toEqual([
      currentYear + 4,
      currentYear + 5,
      currentYear + 6,
      currentYear + 7,
      currentYear + 8,
    ]);
    expect(newTimetableHearingYearDialogComponent.defaultYearSelection).toEqual(currentYear + 4);
  });

  it('should get active year', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(newTimetableHearingYearDialogComponent.getActiveYear(timetableHearingYears)).toEqual(
      currentYear,
    );
  });

  it('should get all planned and archived years', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      newTimetableHearingYearDialogComponent.getAllPlanedAndArchivedYears(timetableHearingYears),
    ).toEqual([timetableHearingYears[1], timetableHearingYears[2], timetableHearingYears[3]]);
  });

  it('should validate that the year is planned or archived', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      newTimetableHearingYearDialogComponent.isYearAlreadyPlannedOrArchived(
        currentYear + 1,
        timetableHearingYears,
      ),
    ).toBeTrue();
  });

  it('should validate that the year is neither planned nor archived', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      newTimetableHearingYearDialogComponent.isYearAlreadyPlannedOrArchived(
        currentYear + 4,
        timetableHearingYears,
      ),
    ).toBeFalse();
  });

  it('should calculate proposed years', () => {
    const timetableHearingYears: TimetableHearingYear[] = getTimetableHearingYears();
    expect(
      newTimetableHearingYearDialogComponent.calculateProposedYears(
        currentYear,
        timetableHearingYears,
      ),
    ).toEqual([
      currentYear + 4,
      currentYear + 5,
      currentYear + 6,
      currentYear + 7,
      currentYear + 8,
    ]);
  });
});
