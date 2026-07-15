import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { DateRangeComponent } from './date-range.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasDateIconComponent, AtlasInfoIconComponent, AtlasLabelFieldComponent } from '@atlas/form';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { TodayAndFutureTimetableHeaderComponent } from './today-and-future-timetable-header/today-and-future-timetable-header.component';
import { By } from '@angular/platform-browser';
import { DateRangeValidator } from '../../validation/date-range/date-range-validator';
import { MatDatepicker } from '@angular/material/datepicker';
import moment from 'moment';
import { TimetableYearChangeInternalService } from '../../../api/service/lidi/timetable-year-change-internal.service';
import { inputBinding, signal } from '@angular/core';

const nextTimetableYearChange = new Date('2024-12-15');

describe('DateRangeComponent', () => {
  let component: DateRangeComponent;
  let fixture: ComponentFixture<DateRangeComponent>;
  let timetableYearChangeServiceMock: Mocked<Pick<TimetableYearChangeInternalService, 'getNextTimetablesYearChange'>>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;

  beforeEach(() => {
    timetableYearChangeServiceMock = {
      getNextTimetablesYearChange: vi.fn().mockReturnValue(of([nextTimetableYearChange])),
    };

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        MatDatepicker,
        DateRangeComponent,
        TodayAndFutureTimetableHeaderComponent,
        AtlasDateIconComponent,
        AtlasFieldErrorComponent,
        AtlasInfoIconComponent,
        AtlasLabelFieldComponent,
        TranslatePipe,
      ],
      providers: [
        { provide: TranslatePipe },
        {
          provide: TimetableYearChangeInternalService,
          useValue: timetableYearChangeServiceMock,
        },
      ],
    });
  });

  beforeEach(() => {
    const formGroupInputName: keyof DateRangeComponent = 'formGroup';
    formGroupInput = signal(
      new FormGroup(
        {
          validFrom: new FormControl(),
          validTo: new FormControl(),
        },
        [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
      )
    );
    fixture = TestBed.createComponent(DateRangeComponent, {
      bindings: [inputBinding(formGroupInputName, formGroupInput)],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('MIN_DATE and MAX_DATE should be defined', () => {
    expect(component.MIN_DATE).toBeDefined();
    expect(component.MAX_DATE).toBeDefined();
  });

  it('MIN_DATE and MAX_DATE should be defined', () => {
    expect(component.MIN_DATE).toBeDefined();
    expect(component.MAX_DATE).toBeDefined();
  });

  it('should open validFrom picker and select today', () => {
    const header = openValidFromPickerAndSelectHeader();
    const todayButton = header[0].queryAll(By.css('button'))[0];

    todayButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.formGroup().controls.validFrom.value).toEqual(moment().startOf('day'));
  });

  it('should open validFrom picker and select future timetable', () => {
    const header = openValidFromPickerAndSelectHeader();
    const futureTimetableButton = header[0].queryAll(By.css('button'))[1];

    futureTimetableButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.formGroup().controls.validFrom.value).toEqual(moment(nextTimetableYearChange).startOf('day'));
  });

  function openValidFromPickerAndSelectHeader() {
    const datePickerToggles = fixture.debugElement.queryAll(By.css('atlas-date-icon'));
    expect(datePickerToggles.length).toEqual(2);

    const validFromToggle = datePickerToggles[0];
    validFromToggle.nativeElement.click();
    fixture.detectChanges();

    return fixture.debugElement.queryAll(By.css('atlas-today-and-future-timetable-header'));
  }

  it('should select validFrom today and validTo today', () => {
    const header = openValidFromPickerAndSelectHeader();
    const todayButton = header[0].queryAll(By.css('button'))[0];
    todayButton.nativeElement.click();
    fixture.detectChanges();

    const datePickerToggles = fixture.debugElement.queryAll(By.css('atlas-date-icon'));
    datePickerToggles[1].nativeElement.click();
    fixture.detectChanges();

    // click on circled today
    fixture.debugElement.queryAll(By.css('.mat-calendar-body-today'))[1].nativeElement.click();
    fixture.detectChanges();

    expect(component.formGroup().controls.validFrom.value.isSame(moment().startOf('day'))).toBe(true);
    expect(component.formGroup().controls.validTo.value.isSame(moment().startOf('day'))).toBe(true);
  });
});
