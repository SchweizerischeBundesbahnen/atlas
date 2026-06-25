import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ReferencePointService } from './reference-point.service';
import { AtlasApiService } from '../../atlas-api.service';
import { PlatformService } from '../platform/platform.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { ReferencePointVersion } from '../../../model/referencePointVersion';
import { ReferencePointAttributeType } from '../../../model/referencePointAttributeType';
import { EMPTY } from 'rxjs';

describe('ReferencePointService', () => {
  let service: ReferencePointService;
  let apiService: AtlasApiService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PlatformService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(ReferencePointService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getReferencePointVersions', () => {
    service.getReferencePointVersions('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: 'ch:1:sloid:7000',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/reference-points/ch:1:sloid:7000');
  });

  it('should createPlatform', () => {
    // given
    const referencePointVersion: ReferencePointVersion = {
      designation: 'Designation',
      referencePointType: ReferencePointAttributeType.MainStationEntrance,
      parentServicePointSloid: 'ch:1:sloid:7000',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
    };

    // when
    service.createReferencePoint(referencePointVersion);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/v1/reference-points',
      referencePointVersion
    );
  });

  it('should updatePlatform', () => {
    // given
    const referencePointVersion: ReferencePointVersion = {
      designation: 'Designation',
      referencePointType: ReferencePointAttributeType.MainStationEntrance,
      parentServicePointSloid: 'ch:1:sloid:7000',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
    };

    // when
    service.updateReferencePoint(1, referencePointVersion);

    // then
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/v1/reference-points/1',
      referencePointVersion
    );
  });
});
