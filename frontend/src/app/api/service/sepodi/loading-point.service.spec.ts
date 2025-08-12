import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { LoadingPointService } from './loading-point.service';
import { CreateLoadingPointVersion } from '../../model/createLoadingPointVersion';

describe('LoadingPointService', () => {
  let service: LoadingPointService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoadingPointService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(LoadingPointService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getLoadingPoint', () => {
    service.getLoadingPoint(123, 234);

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/loading-points/123/234',
    );
  });

  it('should createLoadingPoint', () => {
    service.createLoadingPoint( {} as CreateLoadingPointVersion);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/loading-points',
      {}
    );
  });

  it('should updateLoadingPoint', () => {
    service.updateLoadingPoint(123, {} as CreateLoadingPointVersion);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/loading-points/123',
      {}
    );
  });
});
