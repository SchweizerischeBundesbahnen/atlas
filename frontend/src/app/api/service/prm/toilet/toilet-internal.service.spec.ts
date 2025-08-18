import {TestBed} from '@angular/core/testing';

import {ToiletInternalService} from './toilet-internal.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformInternalService} from "../platform/platform-internal.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";

describe('ToiletInternalService', () => {
  let service: ToiletInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(ToiletInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getToiletOverview', () => {
    service.getToiletOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: '123'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/toilets/overview/123',
    );
  });
});
