import {TestBed} from '@angular/core/testing';

import {ParkingLotInternalService} from './parking-lot-internal.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformInternalService} from "../platform/platform-internal.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";

describe('ParkingLotInternalService', () => {
  let service: ParkingLotInternalService;
  let apiService: AtlasApiService;


  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(ParkingLotInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getParkingLotsOverview', () => {
    service.getParkingLotsOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      parentServicePointSloid: '123'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/parking-lots/overview/123',
    );
  });
});
