import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { LineInternalService } from './line-internal.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';
import { UserService } from '../../../core/auth/user/user.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('LineInternalService', () => {
  let service: LineInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LineInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LineInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'delete').mockImplementation(() => EMPTY);
  });

  it('should getLines', () => {
    const validOn = new Date(2025, 0, 1);

    service.getLines('123', undefined, ['REVOKED', 'DRAFT'], undefined, undefined, undefined, validOn);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      swissLineNumber: '123',
      statusRestrictions: ['REVOKED', 'DRAFT'],
      validOn,
      businessOrganisation: undefined,
      elementRestrictions: undefined,
      typeRestrictions: undefined,
      validToFromDate: undefined,
      searchCriteria: undefined,
      fromDate: undefined,
      toDate: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines', expect.any(HttpParams));
  });

  it('should getLine', () => {
    service.getLine('123');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines/123');
    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ slnid: '123' });
  });

  it('should revokeLine', () => {
    service.revokeLine('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      slnid: '123',
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines/123/revoke');
  });

  it('should deleteLines', () => {
    service.deleteLines('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      slnid: '123',
    });
    expect(apiService.delete).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines/123');
  });

  it('should skipWorkflow', () => {
    service.skipWorkflow(1);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines/versions/1/skip-workflow');
  });

  it('should getLineVersionSnapshot', () => {
    const validOn = new Date(2025, 0, 1);
    service.getLineVersionSnapshot(['123', 'test'], validOn);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      searchCriteria: ['123', 'test'],
      validOn,
      statusChoices: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/lines/workflows',
      expect.any(HttpParams)
    );
  });

  it('should getLineVersionSnapshotById', () => {
    service.getLineVersionSnapshotById(1);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines/workflows/1');
  });

  it('should checkAffectedSublines', () => {
    service.checkAffectedSublines(1, {} as UpdateLineVersionV2);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
      updateLineVersionV2: {},
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/lines/affectedSublines/1', {});
  });
});
