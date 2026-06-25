import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { LoadingPointInternalService } from './loading-point-internal.service';

describe('LoadingPointInternalService', () => {
  let service: LoadingPointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LoadingPointInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LoadingPointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getLoadingPointOverview', () => {
    service.getLoadingPointOverview(123);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/loading-points/123',
      expect.any(HttpParams)
    );
  });
});
