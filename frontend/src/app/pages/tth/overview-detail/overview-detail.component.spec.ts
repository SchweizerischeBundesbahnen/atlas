import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { OverviewDetailComponent } from './overview-detail.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import {
  ContainerTimetableHearingStatementV2,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableHearingStatementDocument,
  TimetableHearingStatementSenderV2,
  TimetableHearingStatementV2,
  TimetableHearingYear,
} from '../../../api';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import moment from 'moment';
import { Component, input } from '@angular/core';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockAtlasFieldErrorComponent,
  MockTableComponent,
} from '../../../app.testing.mocks';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableHearingStatementInternalService } from '../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { TimetableHearingYearInternalService } from '../../../api/service/lidi/timetable-hearing-year-internal.service';
import { TableComponent } from '../../../core/components/table/table.component';
import { MatDialog } from '@angular/material/dialog';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';
import { TthYearInternalService } from '../../../api/service/workflow/tth-year-internal.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';

@Component({
  selector: 'atlas-timetable-hearing-overview-tab-heading',
  template: '<p>MockAppTthOverviewTabHeadingComponent</p>',
  imports: [AppTestingModule],
})
class MockAppTthOverviewTabHeadingComponent {
  readonly cantonShort = input.required<string>();
  readonly foundTimetableHearingYear = input.required<TimetableHearingYear>();
  readonly hearingStatus = input.required<HearingStatus>();
  readonly noActiveTimetableHearingYearFound = input.required<boolean>();
  readonly noTimetableHearingYearFound = input.required<boolean>();
  readonly noPlannedTimetableHearingYearFound = input.required<boolean>();
}

const mockTimetableHearingYearsService: Mocked<Pick<TimetableHearingYearInternalService, 'getHearingYears'>> = {
  getHearingYears: vi.fn(),
};
const mockTimetableHearingStatementsService: Mocked<Pick<TimetableHearingStatementInternalService, 'getStatements'>> = {
  getStatements: vi.fn(),
};
const dialogSpy: Mocked<Pick<MatDialog, 'open'>> = { open: vi.fn() };

let dialogServiceSpy: Mocked<
  Pick<DialogService, 'openDialogDataWithConfirmationResult' | 'openCustomDataWithConfirmationResult'>
>;

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
  statementSender: { emails: new Set('a@b.c') },
  statement: 'Ich hätte gerne mehrere Verbindungen am Abend.',
  documents: [],
  dossierId: 123,
};
const containerTimetableHearingStatement: ContainerTimetableHearingStatementV2 = {
  objects: [timetableHearingStatement, timetableHearingStatement],
  totalCount: 2,
};

const mockTthYearWfServiceSpy: Mocked<Pick<TthYearInternalService, 'startTimetableHearingYear'>> = {
  startTimetableHearingYear: vi.fn(),
};

async function baseTestConfiguration() {
  mockTimetableHearingStatementsService.getStatements.mockReturnValue(of(containerTimetableHearingStatement));

  mockTimetableHearingYearsService.getHearingYears.mockReturnValue(of([hearingYear2000, hearingYear2001]));

  dialogServiceSpy = {
    openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    openCustomDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
  };

  await TestBed.configureTestingModule({
    imports: [
      AppTestingModule,
      OverviewDetailComponent,
      SelectComponent,
      AtlasLabelFieldComponent,
      MockAtlasFieldErrorComponent,
      AtlasSpacerComponent,
      MockAppTthOverviewTabHeadingComponent,
      MockTableComponent,
      MockAtlasButtonComponent,
    ],
    providers: [
      {
        provide: TimetableHearingStatementInternalService,
        useValue: mockTimetableHearingStatementsService,
      },
      {
        provide: TimetableHearingYearInternalService,
        useValue: mockTimetableHearingYearsService,
      },
      {
        provide: TthYearInternalService,
        useValue: mockTthYearWfServiceSpy,
      },
      OverviewToTabShareDataService,
      { provide: TranslatePipe },
      { provide: DisplayDatePipe },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: MatDialog, useValue: dialogSpy },
      { provide: DialogService, useValue: dialogServiceSpy },
    ],
  })
    .overrideComponent(OverviewDetailComponent, {
      remove: { imports: [TableComponent] },
      add: { imports: [MockTableComponent] },
    })
    .compileComponents();

  return TestBed.createComponent(OverviewDetailComponent);
}

describe('TimetableHearingOverviewDetailComponent', () => {
  let component: OverviewDetailComponent;
  let route: ActivatedRoute;
  let fixture: ComponentFixture<OverviewDetailComponent>;
  let overviewToTabService: OverviewToTabShareDataService;

  describe('HearingOverviewTab Active', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      overviewToTabService = TestBed.inject(OverviewToTabShareDataService);

      route.snapshot.data = { hearingStatus: HearingStatus.Active };

      overviewToTabService.setCantonShort('ch');
      overviewToTabService.setTimetableHearingYear(hearingYear2000);
      overviewToTabService.setTimetableHearingYearLoading(false);

      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should open dialog on addNewTimetableHearing()', () => {
      component.addNewTimetableHearing();

      expect(dialogServiceSpy.openCustomDataWithConfirmationResult).toHaveBeenCalledTimes(1);
    });

    it('should open dialog on changeSelectedStatus()', () => {
      component.changeSelectedStatus({
        value: {},
        $event: {},
      });

      expect(dialogServiceSpy.openDialogDataWithConfirmationResult).toHaveBeenCalledTimes(1);
    });

    it('should open dialog on collectingStatusChangeAction()', () => {
      component.selectedItems = [{} as TimetableHearingStatementV2];
      component.collectingStatusChangeAction({} as StatementStatus);

      expect(dialogServiceSpy.openDialogDataWithConfirmationResult).toHaveBeenCalledTimes(1);
    });

    it('should open dialog on collectingCantonDeliveryAction()', () => {
      component.selectedItems = [{} as TimetableHearingStatementV2];
      component.collectingCantonDeliveryAction('be');

      expect(dialogServiceSpy.openDialogDataWithConfirmationResult).toHaveBeenCalledTimes(1);
    });

    it('isSwissCanton false', () => {
      //given
      overviewToTabService.setCantonShort('ag');
      //when
      fixture.detectChanges();
      //then
      expect(component.isSwissCanton()).toBeFalsy();
    });

    it('isSwissCanton true', () => {
      //given
      overviewToTabService.setCantonShort('ch');
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
      overviewToTabService.setCantonShort('ch');
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
      expect(component.tableColumns.length).toEqual(11);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('swissCanton');
      expect(component.tableColumns[2].value).toEqual('id');
      expect(component.tableColumns[3].value).toEqual('statementSender');
      expect(component.tableColumns[4].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[5].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[6].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[7].value).toEqual('topic');
      expect(component.tableColumns[8].value).toEqual('dataProtectionChecked');
      expect(component.tableColumns[9].value).toEqual('documents');
      expect(component.tableColumns[10].value).toEqual('etagVersion');
    });

    it('should display active table columns timetableHearing for Canton CH', () => {
      //given
      overviewToTabService.setCantonShort('ch');
      //when
      component.tableColumns = component.getActiveTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(11);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('swissCanton');
      expect(component.tableColumns[2].value).toEqual('id');
      expect(component.tableColumns[3].value).toEqual('statementSender');
      expect(component.tableColumns[4].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[5].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[6].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[7].value).toEqual('topic');
      expect(component.tableColumns[8].value).toEqual('dataProtectionChecked');
      expect(component.tableColumns[9].value).toEqual('documents');
      expect(component.tableColumns[10].value).toEqual('etagVersion');
    });

    it('should display active table columns timetableHearing for Canton BL', () => {
      //given
      overviewToTabService.setCantonShort('bl');
      //when
      component.tableColumns = component.getActiveTableColumns();
      //then
      expect(component.tableColumns.length).toEqual(10);
      expect(component.tableColumns[0].value).toEqual('statementStatus');
      expect(component.tableColumns[1].value).toEqual('id');
      expect(component.tableColumns[2].value).toEqual('statementSender');
      expect(component.tableColumns[3].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[4].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[5].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[6].value).toEqual('topic');
      expect(component.tableColumns[7].value).toEqual('dataProtectionChecked');
      expect(component.tableColumns[8].value).toEqual('documents');
      expect(component.tableColumns[9].value).toEqual('etagVersion');
    });

    it('should get statements table', async () => {
      //when
      overviewToTabService.setCantonShort('ch');
      fixture.detectChanges();
      //then
      expect(component.timeTableHearingStatements).toEqual([timetableHearingStatement, timetableHearingStatement]);
      expect(component.totalCount).toEqual(2);
      expect(component.isTimetableHearingYearFound()).toBeFalsy();
    });

    it('should return the short form of the Swiss canton', () => {
      const testCanton: SwissCanton = SwissCanton.Bern;
      expect(component.mapToShortCanton(testCanton)).toEqual('BE');
    });

    it('should return the last name of the statement sender', () => {
      const testSender: TimetableHearingStatementSenderV2 = {
        firstName: 'Max',
        lastName: 'Mustermann',
        emails: new Set('muster@muster.com'),
      };
      expect(component.mapToLastname(testSender)).toEqual('Mustermann');
    });

    it('should return true if the documents array is not empty', () => {
      const testDocuments: Array<TimetableHearingStatementDocument> = [
        { id: 1, fileName: 'Document 1', fileSize: 123 },
      ];
      expect(component.isDocumentExisting(testDocuments)).toBe(true);
    });

    it('should open dialog', () => {
      dialogSpy.open.mockReturnValue({
        afterClosed: () => of(null),
      } as ReturnType<MatDialog['open']>);

      component.openTthExportAnonymizationChoiceDialog();

      expect(dialogSpy.open).toHaveBeenCalledTimes(1);
    });

    it('should call downloadCsv(true) when dialog returns isAnonymized=true', () => {
      vi.spyOn(component, 'downloadCsv').mockImplementation(() => {});

      dialogSpy.open.mockReturnValue({
        afterClosed: () => of({ isAnonymized: true }),
      } as ReturnType<MatDialog['open']>);

      component.openTthExportAnonymizationChoiceDialog();

      expect(component.downloadCsv).toHaveBeenCalledTimes(1);
      expect(component.downloadCsv).toHaveBeenCalledWith(true);
    });

    it('should call downloadCsv(false) when dialog returns isAnonymized=false', () => {
      vi.spyOn(component, 'downloadCsv').mockImplementation(() => {});

      dialogSpy.open.mockReturnValue({
        afterClosed: () => of({ isAnonymized: false }),
      } as ReturnType<MatDialog['open']>);

      component.openTthExportAnonymizationChoiceDialog();

      expect(component.downloadCsv).toHaveBeenCalledTimes(1);
      expect(component.downloadCsv).toHaveBeenCalledWith(false);
    });

    describe('collectingActions', () => {
      beforeEach(() => {
        component.statusChangeCollectingActionsEnabled = false;
        component.cantonDeliveryCollectingActionsEnabled = false;
        component.showCollectingActionButton = true;
      });

      it('should enable status change collecting actions when STATUS_CHANGE is selected', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('STATUS_CHANGE');
        expect(component.statusChangeCollectingActionsEnabled).toBe(true);
        expect(component.showCollectingActionButton).toBe(false);
        expect(component.loadData).toHaveBeenCalledTimes(1);
      });

      it('should enable canton delivery collecting actions when CANTON_DELIVERY is selected', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('CANTON_DELIVERY');
        expect(component.cantonDeliveryCollectingActionsEnabled).toBe(true);
        expect(component.showCollectingActionButton).toBe(false);
        expect(component.loadData).toHaveBeenCalledTimes(1);
      });

      it('should not change state when other value is selected', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('OTHER_ACTION');
        expect(component.statusChangeCollectingActionsEnabled).toBe(false);
        expect(component.cantonDeliveryCollectingActionsEnabled).toBe(false);
        expect(component.showCollectingActionButton).toBe(true);
        expect(component.loadData).not.toHaveBeenCalled();
      });

      it('should call loadData only once for STATUS_CHANGE', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('STATUS_CHANGE');
        expect(component.loadData).toHaveBeenCalledTimes(1);
      });

      it('should call loadData only once for CANTON_DELIVERY', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('CANTON_DELIVERY');
        expect(component.loadData).toHaveBeenCalledTimes(1);
      });

      it('should not affect cantonDelivery when STATUS_CHANGE is selected', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('STATUS_CHANGE');
        expect(component.cantonDeliveryCollectingActionsEnabled).toBe(false);
      });

      it('should not affect statusChange when CANTON_DELIVERY is selected', () => {
        vi.spyOn(component, 'loadData').mockImplementation(() => {});
        component.collectingActions('CANTON_DELIVERY');
        expect(component.statusChangeCollectingActionsEnabled).toBe(false);
      });
    });
  });

  describe('HearingOverviewTab Active with checkbox', async () => {
    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
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
      expect(component.tableColumns.length).toEqual(12);
      expect(component.tableColumns[0].value).toEqual('id');
      expect(component.tableColumns[1].value).toEqual('statementStatus');
      expect(component.tableColumns[2].value).toEqual('swissCanton');
      expect(component.tableColumns[3].value).toEqual('id');
      expect(component.tableColumns[4].value).toEqual('statementSender');
      expect(component.tableColumns[5].value).toEqual('responsibleTransportCompaniesDisplay');
      expect(component.tableColumns[6].value).toEqual('timetableFieldNumber');
      expect(component.tableColumns[7].value).toEqual('timetableFieldDescription');
      expect(component.tableColumns[8].value).toEqual('topic');
      expect(component.tableColumns[9].value).toEqual('dataProtectionChecked');
      expect(component.tableColumns[10].value).toEqual('documents');
      expect(component.tableColumns[11].value).toEqual('etagVersion');
    });
  });

  describe('HearingOverviewTab Planned', async () => {
    const hearingYear: TimetableHearingYear = {
      timetableYear: 2000,
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };
    const hearingYears: TimetableHearingYear[] = [hearingYear, hearingYear];
    mockTimetableHearingYearsService.getHearingYears.mockReturnValue(of(hearingYears));

    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      overviewToTabService = TestBed.inject(OverviewToTabShareDataService);

      overviewToTabService.setHearingStatus(HearingStatus.Planned);
      overviewToTabService.setCantonShort('ch');
      overviewToTabService.setTimetableHearingYear(hearingYear2000);
      overviewToTabService.setTimetableHearingYearLoading(false);
      overviewToTabService.setTimetableHearingYearFound(false);
      dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('isHearingYearActive false', () => {
      expect(component.isHearingYearActive()).toBeFalsy();
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
      overviewToTabService.setCantonShort('ch');
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
      overviewToTabService.setCantonShort('bl');
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

    it('should startTimetableHearingYear', () => {
      mockTthYearWfServiceSpy.startTimetableHearingYear.mockReturnValue(of(hearingYear2000));

      component.startTimetableHearing();

      expect(mockTthYearWfServiceSpy.startTimetableHearingYear).toHaveBeenCalledTimes(1);
      expect(mockTthYearWfServiceSpy.startTimetableHearingYear).toHaveBeenCalledWith(2000);
    });
  });

  describe('HearingOverviewTab Archived', async () => {
    const hearingYear: TimetableHearingYear = {
      timetableYear: 2000,
      hearingFrom: moment().toDate(),
      hearingTo: moment().toDate(),
    };

    const hearingYears: TimetableHearingYear[] = [hearingYear, hearingYear];

    mockTimetableHearingYearsService.getHearingYears.mockReturnValue(of(hearingYears));

    beforeEach(async () => {
      fixture = await baseTestConfiguration();
      route = TestBed.inject(ActivatedRoute);
      overviewToTabService = TestBed.inject(OverviewToTabShareDataService);

      overviewToTabService.setHearingStatus(HearingStatus.Archived);
      overviewToTabService.setCantonShort('ch');
      overviewToTabService.setTimetableHearingYear(hearingYear2000);
      overviewToTabService.setTimetableHearingYearLoading(false);
      overviewToTabService.setTimetableHearingYearFound(true);

      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('isHearingYearActive false', () => {
      overviewToTabService.setHearingStatus(HearingStatus.Planned);

      expect(component.isHearingYearActive()).toBeFalsy();
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
      overviewToTabService.setCantonShort('ch');
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
      overviewToTabService.setCantonShort('bl');
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
