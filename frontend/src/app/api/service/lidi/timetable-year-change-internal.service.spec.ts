import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableYearChangeInternalService } from './timetable-year-change-internal.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('TimetableYearChangeInternalService', () => {
  let service: TimetableYearChangeInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TimetableYearChangeInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableYearChangeInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getNextTimetablesYearChange', () => {
    service.getNextTimetablesYearChange(123);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      count: 123,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-year-change/next-years/123'
    );
  });

  it('should getTimetableYearChange', () => {
    service.getTimetableYearChange(2025);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      year: 2025,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/timetable-year-change/2025');
  });
});
