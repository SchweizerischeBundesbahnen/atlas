import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableFieldNumberInternalService } from './timetable-field-number-internal.service';
import any = jasmine.any;

describe('TimetableFieldNumberInternalService', () => {
  let service: TimetableFieldNumberInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TimetableFieldNumberInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(TimetableFieldNumberInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'post');
    spyOn(apiService, 'get');
    spyOn(apiService, 'delete');
  });

  it('should getOverview', () => {
    const validOn = new Date(2025, 0, 1);
    service.getOverview(['123', 'test'], undefined, undefined, validOn);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      validOn,
      businessOrganisation: undefined,
      searchCriteria: ['123', 'test'],
      statusChoices: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
      number: undefined,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/field-numbers',
      any(HttpParams)
    );
  });

  it('should revokeTimetableFieldNumber', () => {
    service.revokeTimetableFieldNumber('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      ttfnId: '123'
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/field-numbers/123/revoke',
    );
  });

  it('should deleteVersions', () => {
    service.deleteVersions('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      ttfnId: '123'
    });
    expect(apiService.delete).toHaveBeenCalledOnceWith(
      '/line-directory/internal/field-numbers/123',
    );
  });
});
