import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableHearingYearInternalService } from './timetable-hearing-year-internal.service';
import { TimetableHearingYear } from '../../model/timetableHearingYear';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('TimetableHearingYearInternalService', () => {
  let service: TimetableHearingYearInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TimetableHearingYearInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableHearingYearInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should createHearingYear', () => {
    service.createHearingYear({} as TimetableHearingYear);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      timetableHearingYear: {},
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/timetable-hearing/years', {});
  });

  it('should getHearingYear', () => {
    service.getHearingYear(2025);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      year: 2025,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/timetable-hearing/years/2025');
  });

  it('should getHearingYears', () => {
    service.getHearingYears(['ACTIVE', 'PLANNED']);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      statusChoices: ['ACTIVE', 'PLANNED'],
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/years',
      expect.any(HttpParams)
    );
  });

  it('should startHearingYear', () => {
    service.startHearingYear(2025);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      year: 2025,
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/years/2025/start'
    );
  });

  it('should updateTimetableHearingSettings', () => {
    service.updateTimetableHearingSettings(2025, {} as TimetableHearingYear);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      year: 2025,
      timetableHearingYear: {},
    });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/timetable-hearing/years/2025', {});
  });
});
