import {TestBed} from '@angular/core/testing';
import {AtlasApiService} from '../atlas-api.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserService} from '../../../core/auth/user/user.service';
import {LoadingPointInternalService} from "./loading-point-internal.service";

describe('LoadingPointInternalService', () => {
  let service: LoadingPointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoadingPointInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(LoadingPointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'get');
  });

  it('should getLoadingPointOverview', () => {
    service.getLoadingPointOverview(123);

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/loading-points/123', jasmine.any(HttpParams)
    );
  });
});
