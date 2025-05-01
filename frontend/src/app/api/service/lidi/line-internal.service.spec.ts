import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { LineInternalService } from './line-internal.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';
import { UserService } from '../../../core/auth/user/user.service';
import any = jasmine.any;

describe('LineInternalService', () => {
  let service: LineInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LineInternalService, AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LineInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'post');
    spyOn(apiService, 'delete');
  });

  it('should revokeLine', () => {
    service.revokeLine('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      slnid: '123',
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/lines/123/revoke',
    );
  });

  it('should deleteLines', () => {
    service.deleteLines('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      slnid: '123',
    });
    expect(apiService.delete).toHaveBeenCalledOnceWith(
      '/line-directory/internal/lines/123',
    );
  });

  it('should skipWorkflow', () => {
    service.skipWorkflow(1);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/lines/versions/1/skip-workflow',
    );
  });

  it('should getLineVersionSnapshot', () => {
    const validOn = new Date(2025, 0, 1);
    service.getLineVersionSnapshot(['123', 'test'], validOn);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      searchCriteria: ['123', 'test'],
      validOn,
      statusChoices: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/lines/workflows',
      any(HttpParams),
    );
  });

  it('should getLineVersionSnapshotById', () => {
    service.getLineVersionSnapshotById(1);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/lines/workflows/1',
    );
  });

  it('should checkAffectedSublines', () => {
    service.checkAffectedSublines(1, {} as UpdateLineVersionV2);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
      updateLineVersionV2: {},
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/lines/affectedSublines/1',
      {},
    );
  });
});
