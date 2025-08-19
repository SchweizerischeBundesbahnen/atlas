import {TestBed} from '@angular/core/testing';

import {PlatformInternalService} from './platform-internal.service';
import {AtlasApiService} from "../../atlas-api.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";

describe('PlatformInternalService', () => {
  let service: PlatformInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(PlatformInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getPlatformOverview', () => {
    service.getPlatformOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      parentSloid: '123'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/platforms/overview/123',
    );
  });
});
