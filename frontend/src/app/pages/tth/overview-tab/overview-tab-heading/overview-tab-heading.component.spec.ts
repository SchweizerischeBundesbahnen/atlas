import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewTabHeadingComponent } from './overview-tab-heading.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { HearingStatus } from '../../../../api';
import moment from 'moment';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { TranslatePipe } from '@ngx-translate/core';

describe('TimetableHearingOverviewTabHeadingComponent', () => {
  let component: OverviewTabHeadingComponent;
  let fixture: ComponentFixture<OverviewTabHeadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OverviewTabHeadingComponent, DisplayDatePipe],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(OverviewTabHeadingComponent);
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

  it('should return when hearingStatus is Active', () => {
    //given
    fixture.componentInstance.hearingStatus = HearingStatus.Active;

    //when
    fixture.detectChanges();

    //then
    expect(component.isHearingStatusActive).toBeTruthy();
  });

  it('should return when hearingStatus is Planned', () => {
    //given
    fixture.componentInstance.hearingStatus = HearingStatus.Planned;

    //when
    fixture.detectChanges();

    //then
    expect(component.isHearingStatusPlanned).toBeTruthy();
  });

  it('should return when hearingStatus is Archived', () => {
    //given
    fixture.componentInstance.hearingStatus = HearingStatus.Archived;

    //when
    fixture.detectChanges();

    //then
    expect(component.isHearingStatusArchived).toBeTruthy();
  });
});
