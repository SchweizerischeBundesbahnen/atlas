import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { LoadingPointService } from './loading-point.service';
import { CreateLoadingPointVersion } from '../../model/createLoadingPointVersion';

describe('LoadingPointService', () => {
  let service: LoadingPointService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LoadingPointService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LoadingPointService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getLoadingPoint', () => {
    service.getLoadingPoint(123, 234);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/loading-points/123/234');
  });

  it('should createLoadingPoint', () => {
    service.createLoadingPoint({} as CreateLoadingPointVersion);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/loading-points', {});
  });

  it('should updateLoadingPoint', () => {
    service.updateLoadingPoint(123, {} as CreateLoadingPointVersion);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/loading-points/123', {});
  });
});
