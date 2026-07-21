import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { ServicePointInternalService } from './service-point-internal.service';

describe('ServicePointInternalService', () => {
  let service: ServicePointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ServicePointInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(ServicePointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should searchServicePoints', () => {
    service.searchServicePoints({ value: 'aoisudhf' });

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/service-point-directory/internal/service-points/search', {
      value: 'aoisudhf',
    });
  });

  it('should validateServicePoint', () => {
    service.validateServicePoint(123);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/service-points/versions/123/skip-workflow'
    );
  });

  it('should revokeServicePoint', () => {
    service.revokeServicePoint(123);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/service-points/123/revoke'
    );
  });

  it('should updateGlobalId', () => {
    service.updateGlobalId(123, { globalId: 'de:05770:1282' });

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/service-points/123/global-id',
      { globalId: 'de:05770:1282' }
    );
  });
});
