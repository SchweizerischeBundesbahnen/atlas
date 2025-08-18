import {TestBed} from '@angular/core/testing';

import {ContactPointInternalService} from './contact-point-internal.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformInternalService} from "../platform/platform-internal.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";

describe('ContactPointInternalService', () => {
  let service: ContactPointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(ContactPointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getContactPointOverview', () => {
    service.getContactPointOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      parentServicePointSloid: '123'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/contact-points/overview/123',
    );
  });
});
