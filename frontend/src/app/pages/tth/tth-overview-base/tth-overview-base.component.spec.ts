import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';
import { TthOverviewBaseComponent } from './tth-overview-base.component';
import { HearingStatus, TimetableHearingYear } from '../../../api';
import moment from 'moment';
import { ActivatedRoute, Router } from '@angular/router';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';
import { TableService } from '../../../core/components/table/table.service';
import { of, Subject } from 'rxjs';
import { TimetableHearingYearInternalService } from '../../../api/service/lidi/timetable-hearing-year-internal.service';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { MatSelectChange } from '@angular/material/select';
import { TthUtils } from '../util/tth-utils';

const mockTimetableHearingYearsService: Mocked<Pick<TimetableHearingYearInternalService, 'getHearingYears'>> = {
  getHearingYears: vi.fn(),
};

const hearingYear2024: TimetableHearingYear = {
  timetableYear: 2024,
  hearingFrom: moment().toDate(),
  hearingTo: moment().toDate(),
};

const hearingYear2025: TimetableHearingYear = {
  timetableYear: 2025,
  hearingFrom: moment().toDate(),
  hearingTo: moment().toDate(),
};

describe('TthOverviewBaseComponent', () => {
  let component: TthOverviewBaseComponent;
  let fixture: ComponentFixture<TthOverviewBaseComponent>;
  let route: ActivatedRoute;
  let tableService: Mocked<Pick<TableService, 'resetTableSettings'>>;
  let overviewToTabService: OverviewToTabShareDataService;
  let routerEventsSubject: Subject<Event>;

  beforeEach(async () => {
    routerEventsSubject = new Subject();

    const routerSpy = {
      navigate: vi.fn(),
      events: routerEventsSubject.asObservable(),
      url: '/tth/ch/active/statements',
    };

    tableService = {
      resetTableSettings: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TthOverviewBaseComponent],
      providers: [
        {
          provide: TimetableHearingYearInternalService,
          useValue: mockTimetableHearingYearsService,
        },
        OverviewToTabShareDataService,
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              data: { hearingStatus: HearingStatus.Active },
              params: { canton: 'ch' },
              queryParams: {},
            },
          },
        },
        { provide: TableService, useValue: tableService },
        { provide: TranslatePipe },
        translateServiceProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TthOverviewBaseComponent);
    component = fixture.componentInstance;
    route = TestBed.inject(ActivatedRoute);
    overviewToTabService = TestBed.inject(OverviewToTabShareDataService);

    mockTimetableHearingYearsService.getHearingYears.mockReturnValue(of([hearingYear2024, hearingYear2025]));

    overviewToTabService.setCantonShort('ZH');
    overviewToTabService.setTimetableHearingYear({
      timetableYear: 2024,
      hearingFrom: new Date(),
      hearingTo: new Date(),
    });
    overviewToTabService.setTimetableHearingYearLoading(false);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should load timetable hearing years for Active status', () => {
      fixture.detectChanges();

      expect(mockTimetableHearingYearsService.getHearingYears).toHaveBeenCalledWith([HearingStatus.Active]);
      expect(component.timetableYear()).toEqual(hearingYear2024);
    });

    it('should set noTimetableHearingYearFound when no years exist', () => {
      mockTimetableHearingYearsService.getHearingYears.mockReturnValue(of([]));

      fixture.detectChanges();

      expect(component.isTimetableHearingYearFound()).toBe(false);
    });
  });

  describe('Hearing Status', () => {
    it('should handle Planned hearing status', () => {
      route.snapshot.data = { hearingStatus: HearingStatus.Planned };

      fixture.detectChanges();

      expect(component.isHearingYearActive()).toBeFalsy();
      expect(component.isHearingYearPlanned()).toBeTruthy();
      expect(mockTimetableHearingYearsService.getHearingYears).toHaveBeenCalledWith([HearingStatus.Planned]);
    });

    it('should handle Archived hearing status', () => {
      route.snapshot.data = { hearingStatus: HearingStatus.Archived };
      vi.spyOn(TthUtils, 'isHearingStatusArchived').mockReturnValue(true);
      vi.spyOn(component, 'initOverviewArchivedTable').mockImplementation(() => {});

      fixture.detectChanges();

      expect(component.hearingStatus()).toBe(HearingStatus.Archived);
    });
  });

  describe('Archived Table Initialization', () => {
    it('should initialize archived table when hearing status is archived', () => {
      route.snapshot.data = { hearingStatus: HearingStatus.Archived };
      vi.spyOn(component, 'initOverviewArchivedTable').mockImplementation(() => {});

      fixture.detectChanges();

      expect(component.initOverviewArchivedTable).toHaveBeenCalledTimes(1);
    });
  });

  describe('changeSelectedCantonFromDropdown', () => {
    beforeEach(() => {
      fixture.detectChanges();
      overviewToTabService.setTimetableHearingYear({
        timetableYear: 2024,
        hearingFrom: new Date(),
        hearingTo: new Date(),
      });
    });

    it('should change canton and navigate', () => {
      const mockSelectChange = {
        value: 'ZH',
      } as MatSelectChange;

      vi.spyOn(overviewToTabService, 'setCantonShort');
      vi.spyOn(component, 'navigateTo').mockImplementation(() => {});

      component.changeSelectedCantonFromDropdown(mockSelectChange);

      expect(overviewToTabService.setCantonShort).toHaveBeenCalledWith('zh');
      expect(component.navigateTo).toHaveBeenCalledWith('zh', 2024);
      expect(tableService.resetTableSettings).toHaveBeenCalledTimes(1);
    });

    it('should convert canton to lowercase', () => {
      const mockSelectChange = {
        value: 'BE',
      } as MatSelectChange;

      vi.spyOn(overviewToTabService, 'setCantonShort');
      vi.spyOn(component, 'navigateTo').mockImplementation(() => {});

      component.changeSelectedCantonFromDropdown(mockSelectChange);

      expect(overviewToTabService.setCantonShort).toHaveBeenCalledWith('be');
      expect(component.navigateTo).toHaveBeenCalledWith('be', 2024);
    });
  });

  describe('changeSelectedYearFromDropdown', () => {
    beforeEach(() => {
      fixture.detectChanges();
      overviewToTabService.setTimetableHearingYear({
        timetableYear: 2024,
        hearingFrom: new Date(),
        hearingTo: new Date(),
      });
      overviewToTabService.setCantonShort('ZH');
    });

    it('should change year and navigate', () => {
      const mockSelectChange = {
        value: 2025,
      } as MatSelectChange;

      vi.spyOn(overviewToTabService, 'setYearSelection');
      vi.spyOn(component, 'navigateTo').mockImplementation(() => {});

      component.changeSelectedYearFromDropdown(mockSelectChange);

      expect(overviewToTabService.setYearSelection).toHaveBeenCalledWith(mockSelectChange.value);
      expect(component.yearSelection()).toBe(2025);
      expect(component.navigateTo).toHaveBeenCalledWith('zh', 2025);
      expect(tableService.resetTableSettings).toHaveBeenCalledTimes(1);
    });

    it('should update timetable hearing year object', () => {
      overviewToTabService.setCantonShort('BE');
      const mockSelectChange = {
        value: 2026,
      } as MatSelectChange;

      vi.spyOn(overviewToTabService, 'setTimetableHearingYear');
      vi.spyOn(component, 'navigateTo').mockImplementation(() => {});

      component.changeSelectedYearFromDropdown(mockSelectChange);

      expect(component.yearSelection()).toBe(2026);
    });
  });
});
