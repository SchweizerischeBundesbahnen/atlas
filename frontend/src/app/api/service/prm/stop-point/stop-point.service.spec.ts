import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { StopPointService } from './stop-point.service';
import { AtlasApiService } from '../../atlas-api.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from '../../../../core/auth/user/user.service';
import { ReadStopPointVersion } from '../../../model/readStopPointVersion';
import { EMPTY } from 'rxjs';

describe('StopPointService', () => {
  let service: StopPointService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StopPointService,
        AtlasApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(StopPointService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getStopPointVersions', () => {
    service.getStopPointVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: 'ch:1:sloid:7000',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/stop-points/ch:1:sloid:7000');
  });

  it('should createStopPoint', () => {
    // given
    const stopPointVersion: ReadStopPointVersion = {
      meansOfTransport: ['METRO'],
      number: {
        number: 8507000,
        numberShort: 7000,
        uicCountryCode: 85,
        checkDigit: 3,
      },
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
      sloid: 'ch:1sloid:700',
      id: 1,
    };

    // when
    service.createStopPoint(stopPointVersion);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/stop-points', stopPointVersion);
  });

  it('should updateStopPoint', () => {
    // given
    const stopPointVersion: ReadStopPointVersion = {
      meansOfTransport: ['METRO'],
      number: {
        number: 8507000,
        numberShort: 7000,
        uicCountryCode: 85,
        checkDigit: 3,
      },
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
      sloid: 'ch:1sloid:700',
      id: 1,
    };

    // when
    service.updateStopPoint(1, stopPointVersion);

    // then
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/stop-points/1', stopPointVersion);
  });
});
