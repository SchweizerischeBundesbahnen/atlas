import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ToiletInternalService } from './toilet-internal.service';
import { AtlasApiService } from '../../atlas-api.service';
import { PlatformInternalService } from '../platform/platform-internal.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

describe('ToiletInternalService', () => {
  let service: ToiletInternalService;
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
    service = TestBed.inject(ToiletInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getToiletOverview', () => {
    service.getToiletOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/internal/toilets/overview/123');
  });
});
