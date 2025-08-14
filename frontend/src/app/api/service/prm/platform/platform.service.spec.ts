import {TestBed} from '@angular/core/testing';

import {PlatformService} from './platform.service';
import {AtlasApiService} from "../../atlas-api.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";
import {ReadPlatformVersion} from "../../../model/readPlatformVersion";

describe('PlatformService', () => {
  let service: PlatformService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(PlatformService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getPlatformVersions', () => {
    service.getPlatformVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: 'ch:1:sloid:7000'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/v1/platfroms/ch:1:sloid:7000',
    );
  });

  it('should createPlatform', () => {
    // given
    const platformVersion: ReadPlatformVersion = {
      number: {
        number: 8507000,
        numberShort: 7000,
        uicCountryCode: 85,
        checkDigit: 3,
      },
      parentServicePointSloid: "ch:1:sloid:7000",
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
    };

    // when
    service.createPlatform(platformVersion);

    // then
    expect(apiService.post)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/platforms', platformVersion);
  });


  it('should updatePlatform', () => {
    // given
    const platformVersion: ReadPlatformVersion = {
      number: {
        number: 8507000,
        numberShort: 7000,
        uicCountryCode: 85,
        checkDigit: 3,
      },
      parentServicePointSloid: "ch:1:sloid:7000",
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
    };

    // when
    service.updatePlatform(1,platformVersion);

    // then
    expect(apiService.put)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/platforms/1', platformVersion);
  });

});
