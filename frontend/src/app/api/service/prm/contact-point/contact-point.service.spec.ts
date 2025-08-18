import {TestBed} from '@angular/core/testing';

import {ContactPointService} from './contact-point.service';
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformService} from "../platform/platform.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";
import {ContactPointVersion} from "../../../model/contactPointVersion";
import {StandardAttributeType} from "../../../model/standardAttributeType";
import {ContactPointType} from "../../../model/contactPointType";

describe('ContactPointService', () => {
  let service: ContactPointService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlatformService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(ContactPointService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getContactPointVersions(', () => {
    service.getContactPointVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: 'ch:1:sloid:7000'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/v1/contact-points/ch:1:sloid:7000',
    );
  });

  it('should createContactPoint', () => {
    // given
    const platformVersion: ContactPointVersion = {
      designation: "Designation",
      inductionLoop: StandardAttributeType.ToBeCompleted,
      type: ContactPointType.InformationDesk,
      wheelchairAccess: StandardAttributeType.Yes,
      parentServicePointSloid: "ch:1:sloid:7000",
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14')
    };

    // when
    service.createContactPoint(platformVersion);

    // then
    expect(apiService.post)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/contact-points', platformVersion);
  });


  it('should updateContactPoint', () => {
    // given
    const platformVersion: ContactPointVersion = {
      designation: "Designation",
      inductionLoop: StandardAttributeType.ToBeCompleted,
      type: ContactPointType.InformationDesk,
      wheelchairAccess: StandardAttributeType.Yes,
      parentServicePointSloid: "ch:1:sloid:7000",
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14')
    };

    // when
    service.updateContactPoint(1,platformVersion);

    // then
    expect(apiService.put)
      .toHaveBeenCalledOnceWith('/prm-directory/v1/contact-points/1', platformVersion);
  });
});
