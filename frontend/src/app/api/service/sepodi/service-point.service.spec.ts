import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { ServicePointService } from './service-point.service';
import { CreateServicePointVersion } from '../../model/createServicePointVersion';
import { UpdateServicePointVersion } from '../../model/updateServicePointVersion';

describe('ServicePointService', () => {
  let service: ServicePointService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ServicePointService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(ServicePointService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getServicePointVersions', () => {
    service.getServicePointVersions(123);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/service-points/123');
  });

  it('should createServicePoint', () => {
    service.createServicePoint({} as CreateServicePointVersion);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/service-points', {});
  });

  it('should updateServicePoint', () => {
    service.updateServicePoint(123, {} as UpdateServicePointVersion);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/service-points/123', {});
  });
});
