import { LineService } from './line.service';
import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpParams } from '@angular/common/http';
import { LineVersionV2 } from '../../model/lineVersionV2';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';
import { AtlasApiService } from '../atlasApi.service';
import { UserService } from '../../../core/auth/user/user.service';
import any = jasmine.any;

describe('LineService', () => {
  let service: LineService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LineService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(LineService);
    apiService = TestBed.inject(AtlasApiService);

    spyOn(apiService, "paramsOf").and.callThrough();
    spyOn(apiService, "validateParams").and.callThrough();
    spyOn(apiService, "get");
    spyOn(apiService, "post");
    spyOn(apiService, "put");
  });

  it('should getLines', () => {
    const validOn = new Date(2025, 0, 1);

    service.getLines('123', undefined, ['REVOKED', 'DRAFT'], undefined, undefined, undefined, validOn);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      swissLineNumber: '123',
      statusRestrictions: ['REVOKED', 'DRAFT'],
      validOn,
      businessOrganisation: undefined,
      elementRestrictions: undefined,
      typeRestrictions: undefined,
      validToFromDate: undefined,
      searchCriteria: undefined,
      modifiedAfter: undefined,
      createdAfter: undefined,
      fromDate: undefined,
      toDate: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/v1/lines',
      any(HttpParams),
    );
  });

  it('should getLine', () => {
    service.getLine('123');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/v1/lines/123',
    );
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ slnid: '123' });
  });

  it('should getLineVersions', () => {
    service.getLineVersions('123');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/v1/lines/versions/123',
    );
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ slnid: '123' });
  });

  it('should getLineVersionsV2', () => {
    service.getLineVersionsV2('123');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/v2/lines/versions/123',
    );
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ slnid: '123' });
  });

  it('should createLineVersionV2', () => {
    service.createLineVersionV2({} as LineVersionV2);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/v2/lines/versions',
      {},
    );
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ lineVersionV2: {} });
  });

  it('should updateLineVersion', () => {
    service.updateLineVersion(1, {} as UpdateLineVersionV2);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/line-directory/v2/lines/versions/1',
      {},
    );
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ id: 1, updateLineVersionV2: {} });
  });
});
