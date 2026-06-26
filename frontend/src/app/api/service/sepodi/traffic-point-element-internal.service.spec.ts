import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TrafficPointElementInternalService } from './traffic-point-element-internal.service';

describe('TrafficPointElementInternalService', () => {
  let service: TrafficPointElementInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TrafficPointElementInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TrafficPointElementInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getAreasOfServicePoint', () => {
    service.getAreasOfServicePoint(7000);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/traffic-point-elements/areas/7000',
      expect.any(HttpParams)
    );
  });

  it('should getAreasOfServicePoint', () => {
    service.getPlatformsOfServicePoint(7000);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/traffic-point-elements/platforms/7000',
      expect.any(HttpParams)
    );
  });

  it('should getTrafficPointsOfServicePointValidToday', () => {
    service.getTrafficPointsOfServicePointValidToday(7000);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/traffic-point-elements/actual-date/7000'
    );
  });

  it('should revoke sector', () => {
    service.revokeTrafficPoint('ch:1:sloid:7000:1');

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/traffic-point-elements/ch%3A1%3Asloid%3A7000%3A1/revoke'
    );
  });
});
