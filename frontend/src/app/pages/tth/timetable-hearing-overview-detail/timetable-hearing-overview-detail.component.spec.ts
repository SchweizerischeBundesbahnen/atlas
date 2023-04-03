import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewDetailComponent } from './timetable-hearing-overview-detail.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { HearingStatus } from '../../../api';
import { ActivatedRoute } from '@angular/router';

async function baseTestConfiguration() {
  await TestBed.configureTestingModule({
    declarations: [TimetableHearingOverviewDetailComponent],
    imports: [AppTestingModule],
    providers: [{ provide: TranslatePipe }, { provide: DisplayDatePipe }],
  }).compileComponents();

  return TestBed.createComponent(TimetableHearingOverviewDetailComponent);
}

describe('TimetableHearingOverviewDetailComponent', () => {
  let component: TimetableHearingOverviewDetailComponent;
  let route: ActivatedRoute;
  let fixture: ComponentFixture<TimetableHearingOverviewDetailComponent>;

  describe('HearingStatus Active', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      route.snapshot.data = { hearingStatus: HearingStatus.Active };
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('isSwissCanton false', () => {
      //given
      component.cantonShort = 'ag';
      //when
      fixture.detectChanges();
      //then
      expect(component.isSwissCanton).toBeFalsy();
    });

    it('isSwissCanton true', () => {
      //given
      component.cantonShort = 'ch';
      //when
      fixture.detectChanges();
      //then
      expect(component.isSwissCanton).toBeTruthy();
    });

    it('isHearingYearActive true', () => {
      expect(component.isHearingYearActive).toBeTruthy();
    });

    it('should display active ch timetableHearing', () => {
      //given
      component.cantonShort = 'ch';
      //when
      fixture.detectChanges();
      //then
      expect(component.showManageTimetableHearingButton).toBeTruthy();
      expect(component.showAddNewStatementButton).toBeFalsy();
      expect(component.showDownloadCsvButton).toBeTruthy();
      expect(component.showStartTimetableHearingButton).toBeFalsy();
      expect(component.showAddNewTimetableHearingButton).toBeFalsy();
      expect(component.showHearingDetail).toBeFalsy();
      expect(component.showDownloadCsvButton).toBeTruthy();
    });

    it('should display active table columns timetableHearing', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.tableColumns.length).toEqual(7);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('swissCanton');
      expect(component.tableColumns[2].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[3].value).toEqual('ttfnid');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('editionDate');
      expect(component.tableColumns[6].value).toEqual('editor');
    });
  });

  describe('HearingStatus Planned', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      route.snapshot.data = { hearingStatus: HearingStatus.Planned };
      component = fixture.componentInstance;
      fixture.componentInstance.noTimetableHearingYearFound = true;
      fixture.detectChanges();
    });

    it('isHearingYearActive false', () => {
      expect(component.isHearingYearActive).toBeFalsy();
    });

    it('should display planned button timetableHearing', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.showAddNewTimetableHearingButton).toBeTruthy();
      expect(component.showStartTimetableHearingButton).toBeTruthy();
      expect(component.showHearingDetail).toBeTruthy();
      expect(component.showAddNewStatementButton).toBeFalsy();
      expect(component.showDownloadCsvButton).toBeFalsy();
    });

    it('should display planned table columns timetableHearing', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.tableColumns.length).toEqual(4);
      expect(component.tableColumns[0].value).toEqual('swissCanton');
      expect(component.tableColumns[1].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[2].value).toEqual('ttfnid');
      expect(component.tableColumns[3].value).toEqual('timetableFieldNumber');
    });
  });

  describe('HearingStatus Archived', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      route.snapshot.data = { hearingStatus: HearingStatus.Archived };
      component = fixture.componentInstance;
      fixture.componentInstance.noTimetableHearingYearFound = true;
      fixture.detectChanges();
    });

    it('isHearingYearActive false', () => {
      expect(component.isHearingYearActive).toBeFalsy();
    });

    it('should display rchived button timetableHearing', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.showManageTimetableHearingButton).toBeFalsy();
      expect(component.showAddNewStatementButton).toBeFalsy();
      expect(component.showStartTimetableHearingButton).toBeFalsy();
      expect(component.showAddNewTimetableHearingButton).toBeFalsy();
      expect(component.showHearingDetail).toBeFalsy();
      expect(component.showDownloadCsvButton).toBeTruthy();
    });

    it('should display archived table columns timetableHearing', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.tableColumns.length).toEqual(5);
      expect(component.tableColumns[0].value).toEqual('swissCanton');
      expect(component.tableColumns[1].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[2].value).toEqual('ttfnid');
      expect(component.tableColumns[3].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[4].value).toEqual('editor');
    });
  });
});
