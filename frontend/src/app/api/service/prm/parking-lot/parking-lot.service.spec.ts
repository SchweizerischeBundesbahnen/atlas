import {TestBed} from '@angular/core/testing';

import {ParkingLotService} from './parking-lot.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformService} from "../platform/platform.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";
import {ReadParkingLotVersion} from "../../../model/readParkingLotVersion";
import {BooleanOptionalAttributeType} from "../../../model/booleanOptionalAttributeType";

describe('ParkingLotService', () => {
  let service: ParkingLotService;
  let apiService: AtlasApiService;


  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(ParkingLotService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getParkingLotVersions', () => {
    service.getParkingLotVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: 'ch:1:sloid:7000'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/v1/parking-lots/ch:1:sloid:7000',
    );
  });

  it('should createPlatform', () => {
    // given
    const parkingLotVersion: ReadParkingLotVersion = {
      designation: "Designation",
      placesAvailable: BooleanOptionalAttributeType.Yes,
      prmPlacesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
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
    service.createParkingLot(parkingLotVersion);

    // then
    expect(apiService.post)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/parking-lots', parkingLotVersion);
  });


  it('should updatePlatform', () => {
    // given
    const parkingLotVersion: ReadParkingLotVersion = {
      designation: "Designation",
      placesAvailable: BooleanOptionalAttributeType.Yes,
      prmPlacesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
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
    service.updateParkingLot(1,parkingLotVersion);

    // then
    expect(apiService.put)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/parking-lots/1', parkingLotVersion);
  });
});
