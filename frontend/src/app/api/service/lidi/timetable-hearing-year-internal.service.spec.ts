import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableHearingYearInternalService } from './timetable-hearing-year-internal.service';
import { TimetableHearingYear } from '../../model/timetableHearingYear';
import any = jasmine.any;

describe('TimetableHearingYearInternalService', () => {
  let service: TimetableHearingYearInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TimetableHearingYearInternalService, AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableHearingYearInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'post');
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
  });

  it('should closeTimetableHearing', () => {
    service.closeTimetableHearing(2025);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      year: 2025,
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/years/2025/close',
    );
  });

  it('should createHearingYear', () => {
    service.createHearingYear({} as TimetableHearingYear);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      timetableHearingYear: {},
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/years',
      {},
    );
  });

  it('should getHearingYear', () => {
    service.getHearingYear(2025);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      year: 2025,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/years/2025',
    );
  });

  it('should getHearingYears', () => {
    service.getHearingYears(['ACTIVE', 'PLANNED']);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      statusChoices: ['ACTIVE', 'PLANNED'],
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/years',
      any(HttpParams),
    );
  });

  it('should startHearingYear', () => {
    service.startHearingYear(2025);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      year: 2025,
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/years/2025/start',
    );
  });

  it('should updateTimetableHearingSettings', () => {
    service.updateTimetableHearingSettings(2025, {} as TimetableHearingYear);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      year: 2025,
      timetableHearingYear: {},
    });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/years/2025',
      {},
    );
  });
});
