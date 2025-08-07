import { TestBed } from '@angular/core/testing';
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
      providers: [ServicePointService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(ServicePointService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getServicePointVersions', () => {
    service.getServicePointVersions(123);

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/service-points/123',
    );
  });

  it('should createServicePoint', () => {
    service.createServicePoint( {} as CreateServicePointVersion);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/service-points',
      {}
    );
  });

  it('should updateServicePoint', () => {
    service.updateServicePoint(123, {} as UpdateServicePointVersion);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/service-points/123',
      {}
    );
  });
});
