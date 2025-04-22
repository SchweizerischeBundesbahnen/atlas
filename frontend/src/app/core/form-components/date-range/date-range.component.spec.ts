import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateRangeComponent } from './date-range.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { DateIconComponent } from '../date-icon/date-icon.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { InfoIconComponent } from '../info-icon/info-icon.component';
import { of } from 'rxjs';
import { TodayAndFutureTimetableHeaderComponent } from './today-and-future-timetable-header/today-and-future-timetable-header.component';
import { By } from '@angular/platform-browser';
import { DateRangeValidator } from '../../validation/date-range/date-range-validator';
import { MatDatepicker } from '@angular/material/datepicker';
import moment from 'moment';
import { TimetableYearChangeInternalService } from '../../../api/service/timetable-year-change-internal.service';

const nextTimetableYearChange = new Date('2024-12-15');
const timetableYearChangeService = jasmine.createSpyObj(
  'TimetableYearChangeInternalService',
  ['getNextTimetablesYearChange']
);
timetableYearChangeService.getNextTimetablesYearChange.and.returnValue(
  of([nextTimetableYearChange])
);

describe('DateRangeComponent', () => {
  let component: DateRangeComponent;
  let fixture: ComponentFixture<DateRangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        MatDatepicker,
        DateRangeComponent,
        TodayAndFutureTimetableHeaderComponent,
        DateIconComponent,
        AtlasFieldErrorComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
      ],
      providers: [
        { provide: TranslatePipe },
        {
          provide: TimetableYearChangeInternalService,
          useValue: timetableYearChangeService,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateRangeComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup(
      {
        validFrom: new FormControl(),
        validTo: new FormControl(),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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

    expect(component.formGroup.controls.validFrom.value).toEqual(
      moment().startOf('day')
    );
  });

  it('should open validFrom picker and select future timetable', () => {
    const header = openValidFromPickerAndSelectHeader();
    const futureTimetableButton = header[0].queryAll(By.css('button'))[1];

    futureTimetableButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.formGroup.controls.validFrom.value).toEqual(
      moment(nextTimetableYearChange).startOf('day')
    );
  });

  function openValidFromPickerAndSelectHeader() {
    const datePickerToggles = fixture.debugElement.queryAll(
      By.css('mat-datepicker-toggle')
    );
    expect(datePickerToggles.length).toEqual(2);

    const validFromToggle = datePickerToggles[0];
    validFromToggle.nativeElement.click();
    fixture.detectChanges();

    return fixture.debugElement.queryAll(
      By.css('today-and-future-timetable-header')
    );
  }

  it('should select validFrom today and validTo today', () => {
    const header = openValidFromPickerAndSelectHeader();
    const todayButton = header[0].queryAll(By.css('button'))[0];
    todayButton.nativeElement.click();
    fixture.detectChanges();

    const datePickerToggles = fixture.debugElement.queryAll(
      By.css('mat-datepicker-toggle')
    );
    datePickerToggles[1].nativeElement.click();
    fixture.detectChanges();

    // click on circled today
    fixture.debugElement
      .queryAll(By.css('.mat-calendar-body-today'))[1]
      .nativeElement.click();
    fixture.detectChanges();

    expect(
      component.formGroup.controls.validFrom.value.isSame(
        moment().startOf('day')
      )
    ).toBeTrue();
    expect(
      component.formGroup.controls.validTo.value.isSame(moment().startOf('day'))
    ).toBeTrue();
  });
});
