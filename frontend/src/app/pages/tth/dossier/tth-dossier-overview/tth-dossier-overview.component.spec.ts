import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TthDossierOverviewComponent } from './tth-dossier-overview.component';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { TableService } from '../../../../core/components/table/table.service';
import { ActivatedRoute, Router } from '@angular/router';
import { OverviewToTabShareDataService } from '../../overview-tab/service/overview-to-tab-share-data.service';
import { HearingStatus, SwissCanton } from '../../../../api';
import { of, throwError } from 'rxjs';
import { Cantons } from '../../../../core/cantons/Cantons';
import { TthDossier } from '../../../../api/model/tthDossier';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { UserService } from '../../../../core/auth/user/user.service';
import { TranslateService } from '@ngx-translate/core';
import { FileDownloadService } from '../../../../core/components/file-upload/file/file-download.service';
import { Language } from '../../../../api/model/language';

describe('TthDossierOverviewComponent', () => {
  const activatedRouteStub = {
    snapshot: {
      params: {},
      queryParams: {},
      data: { hearingStatus: HearingStatus.Active },
    },
    params: of({}),
    queryParams: of({}),
  };

  let component: TthDossierOverviewComponent;
  let dossierService: Mocked<Pick<DossierInternalService, 'getOverview' | 'getDossiersAsCsv'>>;
  let tableService: Mocked<
    Pick<TableService, 'initializeFilterConfig'> & {
      pageIndex: number;
      pageSize: number;
      sortString: string;
      filter: Record<string, { getActiveSearch: () => string | string[] }>;
    }
  >;
  let router: Mocked<Pick<Router, 'navigate'>>;
  let overviewToTabService: OverviewToTabShareDataService;
  let activatedRoute: ActivatedRoute;
  let permissionServiceSpy: Mocked<Pick<PermissionService, 'getTthApplicationUserType'>>;
  let translateService: Mocked<Pick<TranslateService, 'getCurrentLang'>>;
  let userService: Mocked<
    Pick<UserService, 'setCurrentUserAndLoadPermissions'> & {
      currentUser: { email: string; sbbuid: string };
    }
  >;

  function createTestBed() {
    TestBed.configureTestingModule({
      providers: [
        TthDossierOverviewComponent,
        OverviewToTabShareDataService,
        { provide: DossierInternalService, useValue: dossierService },
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userService },
        { provide: TranslateService, useValue: translateService },
        { provide: TableService, useValue: tableService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    });

    component = TestBed.inject(TthDossierOverviewComponent);
    overviewToTabService = TestBed.inject(OverviewToTabShareDataService);

    overviewToTabService.setCantonShort('ZH');
    overviewToTabService.setTimetableHearingYear({
      timetableYear: 2024,
      hearingFrom: new Date(),
      hearingTo: new Date(),
    });
    overviewToTabService.setTimetableHearingYearLoading(false);
  }

  beforeEach(() => {
    dossierService = {
      getOverview: vi.fn(),
      getDossiersAsCsv: vi.fn(),
    };

    permissionServiceSpy = {
      getTthApplicationUserType: vi.fn(),
    };

    translateService = {
      getCurrentLang: vi.fn().mockReturnValue('fr'),
    };

    userService = {
      setCurrentUserAndLoadPermissions: vi.fn(),
      currentUser: { email: 'test@example.com', sbbuid: 'u123456' },
    };

    tableService = {
      initializeFilterConfig: vi.fn(),
      pageIndex: 0,
      pageSize: 10,
      sortString: 'topic,asc',
      filter: {
        chipSearch: { getActiveSearch: () => [] },
        multiSelectDossierStatus: { getActiveSearch: () => [] },
      },
    };

    router = {
      navigate: vi.fn(),
    };

    createTestBed();

    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Getters', () => {
    it('should get canton short from service', () => {
      expect(component.cantonShort()).toBe('ZH');
    });

    it('should get timetable hearing year from service', () => {
      expect(component.timetableYear().timetableYear).toBe(2024);
    });

    it('should return timetableHearingYearFound from service', () => {
      overviewToTabService.setTimetableHearingYearFound(true);
      expect(component.isTimetableHearingYearFound()).toBe(true);
    });

    it('should check if hearing year is active', () => {
      overviewToTabService.setHearingStatus(HearingStatus.Active);
      expect(component.isHearingYearActive()).toBe(true);
    });

    it('should return true when canton is swiss', () => {
      overviewToTabService.setCantonShort('CH');
      expect(component.isSwissCanton()).toBe(true);
    });

    it('should return false when canton is not swiss', () => {
      overviewToTabService.setCantonShort('ZH');
      expect(component.isSwissCanton()).toBe(false);
    });
  });

  describe('loadData', () => {
    it('should initialize table for active hearing status', () => {
      vi.spyOn(component, 'initOverviewTable').mockImplementation(() => {});
      overviewToTabService.setHearingStatus(HearingStatus.Active);
      dossierService.getOverview.mockReturnValue(of({ objects: [], totalCount: 0 }));

      component.loadData();

      expect(component.hearingStatus()).toBe(HearingStatus.Active);
      expect(component.tableColumns).toBeDefined();
      expect(tableService.initializeFilterConfig).toHaveBeenCalled();
    });

    it('should initialize table for archived hearing status', () => {
      activatedRoute.snapshot.data = { hearingStatus: HearingStatus.Archived };
      overviewToTabService.setHearingStatus(HearingStatus.Archived);
      vi.spyOn(component, 'initOverviewTable').mockImplementation(() => {});
      dossierService.getOverview.mockReturnValue(of({ objects: [], totalCount: 0 }));

      component.loadData();

      expect(component.hearingStatus()).toBe(HearingStatus.Archived);
      expect(component.tableColumns).toBeDefined();
    });
  });

  describe('getOverview', () => {
    it('should call getOverview with correct parameters if canton', () => {
      dossierService.getOverview.mockReturnValue(of({ objects: [], totalCount: 0 }));

      component.getOverview({ page: 0, size: 10, sort: 'topic,asc' });

      expect(dossierService.getOverview).toHaveBeenCalledWith(
        2024,
        HearingStatus.Active,
        expect.any(String),
        undefined,
        expect.anything(),
        [],
        0,
        10,
        expect.any(Array)
      );
      expect(component.totalCount).toBe(0);
    });

    it('should call getOverview with correct parameters if bo', () => {
      TestBed.resetTestingModule();
      permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');
      createTestBed();

      dossierService.getOverview.mockReturnValue(of({ objects: [], totalCount: 0 }));

      component.getOverview({ page: 0, size: 10, sort: 'topic,asc' });

      expect(dossierService.getOverview).toHaveBeenCalledWith(
        2024,
        HearingStatus.Active,
        expect.any(String),
        'u123456',
        expect.anything(),
        expect.any(Array),
        0,
        10,
        expect.any(Array)
      );
      expect(component.totalCount).toBe(0);
    });

    it('should update tthDossiers and totalCount on success', () => {
      const mockDossiers: TthDossier[] = [
        {
          id: 1,
          topic: 'Test',
          swissCanton: SwissCanton.Zurich,
          statementIds: [],
          questions: [],
          dossierStatus: 'ADDED',
        },
      ];
      dossierService.getOverview.mockReturnValue(of({ objects: mockDossiers, totalCount: 1 }));

      component.getOverview({ page: 0, size: 10, sort: 'topic,asc' });

      expect(component.tthDossiers).toEqual(mockDossiers);
      expect(component.totalCount).toBe(1);
    });

    it('should handle error gracefully', () => {
      dossierService.getOverview.mockReturnValue(throwError(() => new Error('Test error')));

      component.getOverview({ page: 0, size: 10, sort: '' });

      return new Promise<void>((resolve) => {
        setTimeout(() => {
          expect(component.tthDossiers).toEqual([]);
          resolve();
        }, 100);
      });
    });
  });

  describe('editDossier', () => {
    it('should navigate to edit dossier', async () => {
      router.navigate.mockReturnValue(Promise.resolve(true));

      component.editDossier(123);

      expect(router.navigate).toHaveBeenCalledWith([123], {
        relativeTo: expect.anything(),
      });
    });
  });

  describe('mapToShortCanton', () => {
    it('should map SwissCanton to short format', () => {
      const result = component.mapToShortCanton(SwissCanton.Zurich);
      expect(result).toBeTruthy();
    });

    it('should return undefined for unmapped canton', () => {
      vi.spyOn(Cantons, 'fromSwissCanton').mockReturnValue(undefined);
      const result = component.mapToShortCanton(SwissCanton.Zurich);
      expect(result).toBeUndefined();
    });
  });

  describe('initOverviewTable', () => {
    it('should call getOverview with table service parameters', () => {
      vi.spyOn(component, 'getOverview').mockImplementation(() => {});

      component.initOverviewTable();

      expect(component.getOverview).toHaveBeenCalledWith({
        page: 0,
        size: 10,
        sort: 'topic,asc',
      });
    });
  });

  describe('downloadCsv', () => {
    it('should call getDossiersAsCsv with current filter context and trigger file download', () => {
      const blob = new Blob(['id,topic\n1,Mehr Busse'], { type: 'text/csv' });
      dossierService.getDossiersAsCsv.mockReturnValue(of(blob));
      const fileDownloadSpy = vi.spyOn(FileDownloadService, 'downloadFile').mockImplementation(() => {});

      component.downloadCsv();

      expect(dossierService.getDossiersAsCsv).toHaveBeenCalledExactlyOnceWith(
        Language.Fr,
        2024,
        HearingStatus.Active,
        SwissCanton.Zurich,
        undefined,
        [],
        []
      );
      expect(fileDownloadSpy).toHaveBeenCalledExactlyOnceWith('dossiers.csv', blob);
    });
  });
});
