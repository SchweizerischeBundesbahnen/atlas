import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { ServicePointGeoDataInternalService } from './service-point-geo-data-internal.service';
import { SpatialReference } from '../../model/spatialReference';

describe('ServicePointGeoDataInternalService', () => {
  let service: ServicePointGeoDataInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ServicePointGeoDataInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(ServicePointGeoDataInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getLocationInformation', () => {
    service.getLocationInformation({east: 123,
    north: 123,
    spatialReference: SpatialReference.Lv95});

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/geodata/reverse-geocode', jasmine.any(HttpParams),
    );
  });

});
