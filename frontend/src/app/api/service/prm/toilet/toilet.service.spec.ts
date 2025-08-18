import {TestBed} from '@angular/core/testing';

import {ToiletService} from './toilet.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformService} from "../platform/platform.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";
import {ReadToiletVersion} from "../../../model/readToiletVersion";
import {StandardAttributeType} from "../../../model/standardAttributeType";

describe('ToiletService', () => {
  let service: ToiletService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({providers: [PlatformService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],

    });
    service = TestBed.inject(ToiletService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getToiletVersions', () => {
    service.getToiletVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: 'ch:1:sloid:7000'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/v1/toilets/ch:1:sloid:7000',
    );
  });

  it('should createToiletVersion', () => {
    // given
    const toiletVersion: ReadToiletVersion = {
      designation: "Designation",
      wheelchairToilet: StandardAttributeType.Yes,
      number: {
        number: 8507000,
        numberShort: 7000,
        uicCountryCode: 85,
        checkDigit: 3,
      },
      parentServicePointSloid: "ch:1:sloid:7000",
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14')
    };

    // when
    service.createToiletVersion(toiletVersion);

    // then
    expect(apiService.post)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/toilets', toiletVersion);
  });


  it('should updatePlatform', () => {
    // given
    const toiletVersion: ReadToiletVersion = {
      designation: "Designation",
      wheelchairToilet: StandardAttributeType.Yes,
      number: {
        number: 8507000,
        numberShort: 7000,
        uicCountryCode: 85,
        checkDigit: 3,
      },
      parentServicePointSloid: "ch:1:sloid:7000",
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14')
    };

    // when
    service.updateToiletVersion(1,toiletVersion);

    // then
    expect(apiService.put)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/toilets/1', toiletVersion);
  });
});
