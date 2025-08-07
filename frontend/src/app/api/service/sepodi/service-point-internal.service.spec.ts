import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { ServicePointInternalService } from './service-point-internal.service';

describe('ServicePointInternalService', () => {
  let service: ServicePointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ServicePointInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(ServicePointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should searchServicePoints', () => {
    service.searchServicePoints({value: 'aoisudhf'});

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/service-points/search',{value: 'aoisudhf'}
    );
  });

  it('should validateServicePoint', () => {
    service.validateServicePoint(123);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/service-points/versions/123/skip-workflow',
    );
  });

  it('should revokeServicePoint', () => {
    service.revokeServicePoint(123);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/service-points/123/revoke',
    );
  });

});
