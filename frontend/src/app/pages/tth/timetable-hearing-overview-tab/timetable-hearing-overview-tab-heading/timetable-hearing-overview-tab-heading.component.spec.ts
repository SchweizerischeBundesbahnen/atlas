import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewTabHeadingComponent } from './timetable-hearing-overview-tab-heading.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { HearingStatus } from '../../../../api';
import moment from 'moment';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { TranslatePipe } from '@ngx-translate/core';

describe('TimetableHearingOverviewTabHeadingComponent', () => {
  let component: TimetableHearingOverviewTabHeadingComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewTabHeadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewTabHeadingComponent],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }, { provide: DisplayDatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewTabHeadingComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.cantonShort = 'BE';
    fixture.componentInstance.hearingStatus = HearingStatus.Active;
    fixture.componentInstance.foundTimetableHearingYear = {
      timetableYear: 2000,
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
