import { LineService } from './line.service';
import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { LineVersionV2 } from '../../model/lineVersionV2';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';
import { AtlasApiService } from '../atlas-api.service';
import { UserService } from '../../../core/auth/user/user.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('LineService', () => {
  let service: LineService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LineService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LineService);
    apiService = TestBed.inject(AtlasApiService);

    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should getLineVersionsV2', () => {
    service.getLineVersionsV2('123');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/v2/lines/versions/123');
    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ slnid: '123' });
  });

  it('should createLineVersionV2', () => {
    service.createLineVersionV2({} as LineVersionV2);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/v2/lines/versions', {});
    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ lineVersionV2: {} });
  });

  it('should updateLineVersion', () => {
    service.updateLineVersion(1, {} as UpdateLineVersionV2);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/line-directory/v2/lines/versions/1', {});
    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ id: 1, updateLineVersionV2: {} });
  });
});
