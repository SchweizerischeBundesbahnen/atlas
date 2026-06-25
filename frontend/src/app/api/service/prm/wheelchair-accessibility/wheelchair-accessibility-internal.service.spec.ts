import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AtlasApiService } from '../../atlas-api.service';
import { PlatformInternalService } from '../platform/platform-internal.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';
import { WheelchairAccessibilityInternalService } from './wheelchair-accessibility-internal.service';

describe('WheelchairAccessibilityInternalService', () => {
  let service: WheelchairAccessibilityInternalService;
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
    service = TestBed.inject(WheelchairAccessibilityInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getPlatformAccessibilityToday', () => {
    service.getPlatformAccessibilityToday('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/internal/wheelchair-accessibility/platform/123/today'
    );
  });

  it('should getPlatformAccessibility', () => {
    service.getPlatformAccessibility('123', new Date());

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/internal/wheelchair-accessibility/platform/123',
      expect.any(HttpParams)
    );
  });

  it('should getStopPointAccessibilityToday', () => {
    service.getStopPointAccessibilityToday('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/internal/wheelchair-accessibility/stop-point/123/today'
    );
  });

  it('should getStopPointAccessibility', () => {
    service.getStopPointAccessibility('123', new Date());

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/internal/wheelchair-accessibility/stop-point/123',
      expect.any(HttpParams)
    );
  });
});
