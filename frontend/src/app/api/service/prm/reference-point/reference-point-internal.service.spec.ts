import {TestBed} from '@angular/core/testing';

import {ReferencePointInternalService} from './reference-point-internal.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformInternalService} from "../platform/platform-internal.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";

describe('ReferencePointInternalService', () => {
  let service: ReferencePointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(ReferencePointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getPlatformOverview', () => {
    service.getReferencePointsOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      parentServicePointSloid: '123'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/reference-points/overview/123',
    );
  });
});
