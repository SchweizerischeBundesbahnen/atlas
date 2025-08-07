import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TrafficPointElementInternalService } from './traffic-point-element-internal.service';

describe('TrafficPointElementInternalService', () => {
  let service: TrafficPointElementInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TrafficPointElementInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(TrafficPointElementInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getAreasOfServicePoint', () => {
    service.getAreasOfServicePoint(7000);

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/traffic-point-elements/areas/7000', jasmine.any(HttpParams)
    );
  });

  it('should getAreasOfServicePoint', () => {
    service.getPlatformsOfServicePoint(7000);

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/traffic-point-elements/platforms/7000', jasmine.any(HttpParams)
    );
  });

  it('should getTrafficPointsOfServicePointValidToday', () => {
    service.getTrafficPointsOfServicePointValidToday(7000);

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/traffic-point-elements/actual-date/7000',
    );
  });

});
