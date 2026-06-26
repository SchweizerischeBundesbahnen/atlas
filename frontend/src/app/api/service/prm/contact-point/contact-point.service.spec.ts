import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { ContactPointService } from './contact-point.service';
import { AtlasApiService } from '../../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { ContactPointVersion } from '../../../model/contactPointVersion';
import { StandardAttributeType } from '../../../model/standardAttributeType';
import { ContactPointType } from '../../../model/contactPointType';

describe('ContactPointService', () => {
  let service: ContactPointService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ContactPointService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(ContactPointService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getContactPointVersions(', () => {
    service.getContactPointVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: 'ch:1:sloid:7000',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/contact-points/ch:1:sloid:7000');
  });

  it('should createContactPoint', () => {
    // given
    const platformVersion: ContactPointVersion = {
      designation: 'Designation',
      inductionLoop: StandardAttributeType.ToBeCompleted,
      type: ContactPointType.InformationDesk,
      wheelchairAccess: StandardAttributeType.Yes,
      parentServicePointSloid: 'ch:1:sloid:7000',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
    };

    // when
    service.createContactPoint(platformVersion);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/contact-points', platformVersion);
  });

  it('should updateContactPoint', () => {
    // given
    const platformVersion: ContactPointVersion = {
      designation: 'Designation',
      inductionLoop: StandardAttributeType.ToBeCompleted,
      type: ContactPointType.InformationDesk,
      wheelchairAccess: StandardAttributeType.Yes,
      parentServicePointSloid: 'ch:1:sloid:7000',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
    };

    // when
    service.updateContactPoint(1, platformVersion);

    // then
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/contact-points/1', platformVersion);
  });
});
