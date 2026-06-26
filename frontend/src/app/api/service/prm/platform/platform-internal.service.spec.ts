import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { PlatformInternalService } from './platform-internal.service';
import { AtlasApiService } from '../../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

describe('PlatformInternalService', () => {
  let service: PlatformInternalService;
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
    service = TestBed.inject(PlatformInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getPlatformOverview', () => {
    service.getPlatformOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      parentSloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/internal/platforms/overview/123');
  });
});
