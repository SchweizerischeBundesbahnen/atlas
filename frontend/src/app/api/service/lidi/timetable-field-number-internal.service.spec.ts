import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableFieldNumberInternalService } from './timetable-field-number-internal.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('TimetableFieldNumberInternalService', () => {
  let service: TimetableFieldNumberInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TimetableFieldNumberInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableFieldNumberInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'delete').mockImplementation(() => EMPTY);
  });

  it('should getOverview', () => {
    const validOn = new Date(2025, 0, 1);
    service.getOverview(['123', 'test'], undefined, undefined, validOn);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      validOn,
      businessOrganisation: undefined,
      searchCriteria: ['123', 'test'],
      statusChoices: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
      number: undefined,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/field-numbers',
      expect.any(HttpParams)
    );
  });

  it('should revokeTimetableFieldNumber', () => {
    service.revokeTimetableFieldNumber('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      ttfnId: '123',
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/field-numbers/123/revoke');
  });

  it('should deleteVersions', () => {
    service.deleteVersions('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      ttfnId: '123',
    });
    expect(apiService.delete).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/field-numbers/123');
  });
});
