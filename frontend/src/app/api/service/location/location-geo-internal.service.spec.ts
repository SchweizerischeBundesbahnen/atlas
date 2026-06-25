import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { LocationGeoInternalService } from './location-geo-internal.service';
import { SpatialReference } from '../../model/spatialReference';

describe('LocationGeoInternalService', () => {
  let service: LocationGeoInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LocationGeoInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LocationGeoInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getLocationInformation', () => {
    const coordinatePair = {
      east: 123,
      north: 123,
      spatialReference: SpatialReference.Lv95,
    };
    service.getLocationInformation(coordinatePair);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ ...coordinatePair, includeHeight: true });
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({ ...coordinatePair, includeHeight: true });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/location/internal/geo-reference', expect.any(HttpParams));
  });

  it('should getHeight', () => {
    const coordinatePair = {
      east: 123,
      north: 123,
      spatialReference: SpatialReference.Lv95,
    };
    service.getHeight(coordinatePair);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith(coordinatePair);
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith(coordinatePair);
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/location/internal/geo-reference/height',
      expect.any(HttpParams)
    );
  });
});
