import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ReferencePointInternalService } from './reference-point-internal.service';
import { AtlasApiService } from '../../atlas-api.service';
import { PlatformInternalService } from '../platform/platform-internal.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

describe('ReferencePointInternalService', () => {
  let service: ReferencePointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PlatformInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(ReferencePointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getPlatformOverview', () => {
    service.getReferencePointsOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      parentServicePointSloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/internal/reference-points/overview/123');
  });
});
