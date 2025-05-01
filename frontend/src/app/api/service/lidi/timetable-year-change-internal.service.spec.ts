import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableYearChangeInternalService } from './timetable-year-change-internal.service';

describe('TimetableYearChangeInternalService', () => {
  let service: TimetableYearChangeInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TimetableYearChangeInternalService, AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableYearChangeInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getNextTimetablesYearChange', () => {
    service.getNextTimetablesYearChange(123);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      count: 123,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-year-change/next-years/123',
    );
  });

  it('should getTimetableYearChange', () => {
    service.getTimetableYearChange(2025);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      year: 2025,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-year-change/2025',
    );
  });
});
