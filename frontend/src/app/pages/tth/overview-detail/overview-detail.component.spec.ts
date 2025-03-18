import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OverviewDetailComponent} from './overview-detail.component';
import {AppTestingModule} from '../../../app.testing.module';
import {TranslatePipe} from '@ngx-translate/core';
import {DisplayDatePipe} from '../../../core/pipe/display-date.pipe';
import {
  ContainerTimetableHearingStatementV2,
  HearingStatus,
  SwissCanton,
  TimetableHearingStatementDocument, TimetableHearingStatementSenderV2,
  TimetableHearingStatementsService, TimetableHearingStatementV2,
  TimetableHearingYear,
  TimetableHearingYearsService,
} from '../../../api';
import {ActivatedRoute, Router} from '@angular/router';
import {of} from 'rxjs';
import moment from 'moment';
import {Pages} from '../../pages';
import {Component, Input} from '@angular/core';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockAtlasFieldErrorComponent,
  MockTableComponent,
} from '../../../app.testing.mocks';
import {SelectComponent} from '../../../core/form-components/select/select.component';
import {AtlasSpacerComponent} from '../../../core/components/spacer/atlas-spacer.component';
import {TableService} from '../../../core/components/table/table.service';
import {AtlasLabelFieldComponent} from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {PermissionService} from "../../../core/auth/permission/permission.service";

@Component({
    selector: 'app-timetable-hearing-overview-tab-heading',
    template: '<p>MockAppTthOverviewTabHeadingComponent</p>',
    imports: [AppTestingModule]
})
class MockAppTthOverviewTabHeadingComponent {
  @Input() cantonShort!: string;
  @Input() foundTimetableHearingYear!: TimetableHearingYear;
  @Input() hearingStatus!: HearingStatus;
  @Input() noActiveTimetableHearingYearFound!: boolean;
  @Input() noTimetableHearingYearFound!: boolean;
  @Input() noPlannedTimetableHearingYearFound!: boolean;
}

const mockTimetableHearingYearsService = jasmine.createSpyObj('timetableHearingYearsService', [
  'getHearingYears',
]);
const mockTimetableHearingStatementsService = jasmine.createSpyObj(
  'timetableHearingStatementsService',
  ['getStatements'],
);

let router: Router;

const hearingYear2000: TimetableHearingYear = {
  timetableYear: 2000,
  hearingFrom: moment().toDate(),
  hearingTo: moment().toDate(),
};
const hearingYear2001: TimetableHearingYear = {
  timetableYear: 2001,
  hearingFrom: moment().toDate(),
  hearingTo: moment().toDate(),
};

const timetableHearingStatement: TimetableHearingStatementV2 = {
  timetableYear: 2001,
  statementStatus: 'REVOKED',
  ttfnid: 'ch:1:ttfnid:1000008',
  timetableFieldNumber: 'ch:1:ttfnid:1000008',
  swissCanton: 'BASEL_COUNTRY',
  responsibleTransportCompanies: [
    {
      id: 1000,
      number: '#0001',
      abbreviation: 'SBB',
      businessRegisterName: 'Schweizerische Bundesbahnen SBB',
    },
    {
      id: 1001,
      number: '#0001',
      abbreviation: 'Post Auto',
      businessRegisterName: 'Post Auto',
    },
    {
      id: 1002,
      number: '#0001',
      abbreviation: 'BLS',
      businessRegisterName: 'BLS',
    },
  ],
  statementSender: { emails: new Set('a@b.c')},
  statement: 'Ich hätte gerne mehrere Verbindungen am Abend.',
  documents: [],
};
const containerTimetableHearingStatement: ContainerTimetableHearingStatementV2 = {
  objects: [timetableHearingStatement, timetableHearingStatement],
  totalCount: 2,
};

async function baseTestConfiguration() {
  mockTimetableHearingStatementsService.getStatements.and.returnValue(
    of(containerTimetableHearingStatement),
  );

  mockTimetableHearingYearsService.getHearingYears.and.returnValue(
    of([hearingYear2000, hearingYear2001]),
  );

  await TestBed.configureTestingModule({
    imports: [AppTestingModule, OverviewDetailComponent,
        SelectComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasSpacerComponent,
        MockAppTthOverviewTabHeadingComponent,
        MockTableComponent,
        MockAtlasButtonComponent],
    providers: [
        {
            provide: TimetableHearingStatementsService,
            useValue: mockTimetableHearingStatementsService,
        },
        { provide: TimetableHearingYearsService, useValue: mockTimetableHearingYearsService },
        { provide: TranslatePipe },
        { provide: DisplayDatePipe },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: TableService },
    ],
}).compileComponents();

  return TestBed.createComponent(OverviewDetailComponent);
}

describe('TimetableHearingOverviewDetailComponent', () => {
  let component: OverviewDetailComponent;
  let route: ActivatedRoute;
  let fixture: ComponentFixture<OverviewDetailComponent>;

  describe('HearingOverviewTab Active', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      router = TestBed.inject(Router);
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
      expect(component.tableColumns.length).toEqual(10);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('swissCanton');
      expect(component.tableColumns[2].value).toEqual('id');
      expect(component.tableColumns[3].value).toEqual('statementSender');
      expect(component.tableColumns[4].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[5].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[6].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[7].value).toEqual('editionDate');
      expect(component.tableColumns[8].value).toEqual('documents');
      expect(component.tableColumns[9].value).toEqual('etagVersion');
    });

    it('should display active table columns timetableHearing for Canton CH', () => {
      //given
      component.cantonShort = 'ch';
      //when
      component.tableColumns = component.getActiveTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(10);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('swissCanton');
      expect(component.tableColumns[2].value).toEqual('id');
      expect(component.tableColumns[3].value).toEqual('statementSender');
      expect(component.tableColumns[4].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[5].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[6].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[7].value).toEqual('editionDate');
      expect(component.tableColumns[8].value).toEqual('documents');
      expect(component.tableColumns[9].value).toEqual('etagVersion');
    });

    it('should display active table columns timetableHearing for Canton BL', () => {
      //given
      component.cantonShort = 'bl';
      //when
      component.tableColumns = component.getActiveTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(9);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('id');
      expect(component.tableColumns[2].value).toEqual('statementSender');
      expect(component.tableColumns[3].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[6].value).toEqual('editionDate');
      expect(component.tableColumns[7].value).toEqual('documents');
      expect(component.tableColumns[8].value).toEqual('etagVersion');
    });

    it('should get statements table', async () => {
      //when
      component.cantonShort = 'ch';
      fixture.detectChanges();
      //then
      expect(component.timeTableHearingStatements).toEqual([
        timetableHearingStatement,
        timetableHearingStatement,
      ]);
      expect(component.totalCount$).toEqual(2);
      expect(component.noTimetableHearingYearFound).toBeFalsy();
      expect(component.defaultDropdownCantonSelection).toBe('CH');
    });

    it('should set FoundHearingYear from queryParam if exists', () => {
      //given
      const routerNavigateSpy = spyOn(router, 'navigate');
      //when
      component.setFoundHearingYear([hearingYear2000, hearingYear2001]);
      //then
      expect(component.foundTimetableHearingYear).toBe(hearingYear2000);
      expect(component.yearSelection).toBe(hearingYear2000.timetableYear);
      expect(routerNavigateSpy).not.toHaveBeenCalled();
    });

    it('should not set FoundHearingYear from queryParam if does not exists', () => {
      //given
      const routerNavigateSpy = spyOn(router, 'navigate').and.returnValue(
        new Promise((resolve) => {
          resolve(true);
        }),
      );
      route.snapshot.queryParams = { year: 2002 };
      //when
      component.setFoundHearingYear([hearingYear2000, hearingYear2001]);
      //then
      expect(component.foundTimetableHearingYear).toBe(hearingYear2000);
      expect(component.yearSelection).toBe(hearingYear2000.timetableYear);
      expect(routerNavigateSpy).toHaveBeenCalledWith([
        Pages.TTH.path,
        'ch',
        HearingStatus.Active.toLowerCase(),
      ]);
    });

    it('should return the short form of the Swiss canton', () => {
      const testCanton: SwissCanton = SwissCanton.Bern;
      expect(component.mapToShortCanton(testCanton)).toEqual('BE');
    });

    it('should return the last name of the statement sender', () => {
      const testSender: TimetableHearingStatementSenderV2 = { firstName: 'Max', lastName: 'Mustermann', emails: new Set('muster@muster.com')};
      expect(component.mapToLastname(testSender)).toEqual('Mustermann');
    });

    it('should return true if the documents array is not empty', () => {
      const testDocuments: Array<TimetableHearingStatementDocument> = [{ id: 1, fileName: 'Document 1', fileSize: 123 }];
      expect(component.isDocumentExisting(testDocuments)).toBeTrue();
    });
  });

  describe('HearingOverviewTab Active with checkbox', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      router = TestBed.inject(Router);
      route.snapshot.data = { hearingStatus: HearingStatus.Active };
      component = fixture.componentInstance;
      component.cantonDeliveryCollectingActionsEnabled = true;
      component.statusChangeCollectingActionsEnabled = true;
      fixture.detectChanges();
    });

    it('should display active table columns timetableHearing with checkbox', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.tableColumns.length).toEqual(11);
      expect(component.tableColumns[0].value).toEqual('id');
      expect(component.tableColumns[1].value).toEqual('statementStatus');
      expect(component.tableColumns[2].value).toEqual('swissCanton');
      expect(component.tableColumns[3].value).toEqual('id');
      expect(component.tableColumns[4].value).toEqual('statementSender');
      expect(component.tableColumns[5].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[6].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[7].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[8].value).toEqual('editionDate');
      expect(component.tableColumns[9].value).toEqual('documents');
      expect(component.tableColumns[10].value).toEqual('etagVersion');
    });
  });

  describe('HearingOverviewTab Planned', async () => {
    const hearingYear: TimetableHearingYear = {
      timetableYear: 2000,
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    const hearingYears: TimetableHearingYear[] = [hearingYear, hearingYear];
    mockTimetableHearingYearsService.getHearingYears.and.returnValue(of(hearingYears));
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
      expect(component.showStartTimetableHearingButton).toBeFalsy();
      expect(component.showHearingDetail).toBeTruthy();
      expect(component.showAddNewStatementButton).toBeFalsy();
      expect(component.showDownloadCsvButton).toBeFalsy();
    });

    it('should display planned table columns timetableHearing', () => {
      //when
      fixture.detectChanges();
      //then
      expect(component.tableColumns.length).toEqual(7);
      expect(component.tableColumns[0].value).toEqual('swissCanton');
      expect(component.tableColumns[1].value).toEqual('id');
      expect(component.tableColumns[2].value).toEqual('statementSender');
      expect(component.tableColumns[3].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[6].value).toEqual('documents');
    });

    it('should display planned table columns timetableHearing for Canton CH', () => {
      //given
      component.cantonShort = 'ch';
      //when
      component.tableColumns = component.getPlannedOrArchivedTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(7);
      expect(component.tableColumns[0].value).toEqual('swissCanton');
      expect(component.tableColumns[1].value).toEqual('id');
      expect(component.tableColumns[2].value).toEqual('statementSender');
      expect(component.tableColumns[3].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[6].value).toEqual('documents');
    });

    it('should display planned table columns timetableHearing for Canton BL', () => {
      //given
      component.cantonShort = 'bl';
      //when
      component.tableColumns = component.getPlannedOrArchivedTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(6);
      expect(component.tableColumns[0].value).toEqual('id');
      expect(component.tableColumns[1].value).toEqual('statementSender');
      expect(component.tableColumns[2].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[3].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[4].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[5].value).toEqual('documents');
    });

  });

  describe('HearingOverviewTab Archived', async () => {
    const hearingYear: TimetableHearingYear = {
      timetableYear: 2000,
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    const hearingYears: TimetableHearingYear[] = [hearingYear, hearingYear];
    mockTimetableHearingYearsService.getHearingYears.and.returnValue(of(hearingYears));
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

    it('should display archived button timetableHearing', () => {
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
      expect(component.tableColumns.length).toEqual(7);
      expect(component.tableColumns[0].value).toEqual('swissCanton');
      expect(component.tableColumns[1].value).toEqual('id');
      expect(component.tableColumns[2].value).toEqual('statementSender');
      expect(component.tableColumns[3].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[6].value).toEqual('documents');
    });

    it('should display archived table columns timetableHearing for Canton CH', () => {
      //given
      component.cantonShort = 'ch';
      //when
      component.tableColumns = component.getPlannedOrArchivedTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(7);
      expect(component.tableColumns[0].value).toEqual('swissCanton');
      expect(component.tableColumns[1].value).toEqual('id');
      expect(component.tableColumns[2].value).toEqual('statementSender');
      expect(component.tableColumns[3].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[6].value).toEqual('documents');
    });

    it('should display archived table columns timetableHearing for Canton BL', () => {
      //given
      component.cantonShort = 'bl';
      //when
      component.tableColumns = component.getPlannedOrArchivedTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(6);
      expect(component.tableColumns[0].value).toEqual('id');
      expect(component.tableColumns[1].value).toEqual('statementSender');
      expect(component.tableColumns[2].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[3].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[4].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[5].value).toEqual('documents');
    });
  });
});
