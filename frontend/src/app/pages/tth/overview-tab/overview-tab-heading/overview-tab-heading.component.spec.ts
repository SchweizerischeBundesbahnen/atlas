import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { OverviewTabHeadingComponent } from './overview-tab-heading.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { HearingStatus } from '../../../../api';
import moment from 'moment';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { OverviewToTabShareDataService } from '../service/overview-to-tab-share-data.service';
import { inputBinding } from '@angular/core';

describe('TimetableHearingOverviewTabHeadingComponent', () => {
  let component: OverviewTabHeadingComponent;
  let fixture: ComponentFixture<OverviewTabHeadingComponent>;
  let service: OverviewToTabShareDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule, OverviewTabHeadingComponent, DisplayDatePipe],
      providers: [{ provide: TranslatePipe }, OverviewToTabShareDataService],
    });

    const isPlannedTimetableHearingYearFoundInputName: keyof OverviewTabHeadingComponent =
      'isPlannedTimetableHearingYearFound';
    const isTimetableHearingYearFoundInputName: keyof OverviewTabHeadingComponent = 'isTimetableHearingYearFound';
    fixture = TestBed.createComponent(OverviewTabHeadingComponent, {
      bindings: [
        inputBinding(isPlannedTimetableHearingYearFoundInputName, () => false),
        inputBinding(isTimetableHearingYearFoundInputName, () => false),
      ],
    });
    component = fixture.componentInstance;
    fixture.componentInstance.cantonShort = 'BE';
    fixture.componentInstance.hearingStatus = HearingStatus.Active;
    fixture.componentInstance.foundTimetableHearingYear = {
      timetableYear: 2000,
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    service = TestBed.inject(OverviewToTabShareDataService);

    fixture.detectChanges();
  });

  it('should return when hearingStatus is Active', () => {
    service.setHearingStatus(HearingStatus.Active);

    expect(component.isHearingYearActive()).toBeTruthy();
  });

  it('should return when hearingStatus is Planned', () => {
    service.setHearingStatus(HearingStatus.Planned);

    expect(component.isHearingYearPlanned()).toBeTruthy();
  });

  it('should return when hearingStatus is Archived', () => {
    service.setHearingStatus(HearingStatus.Archived);

    expect(component.isHearingYearArchived()).toBeTruthy();
  });
});
