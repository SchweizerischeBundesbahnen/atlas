import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { SectorGroupService } from './sector-group.service';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { CreateSectorGroupVersion } from '../../model/createSectorGroupVersion';
import { ReadSectorGroupVersion } from '../../model/readSectorGroupVersion';

describe('SectorGroupService', () => {
  let service: SectorGroupService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SectorGroupService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(SectorGroupService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getSectorGroups', () => {
    service.getSectorGroup('ch:1:sloid:7000:1');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/v1/sector-groups/ch%3A1%3Asloid%3A7000%3A1'
    );
  });

  it('should createSectorGroup', () => {
    service.createSectorGroup({} as CreateSectorGroupVersion);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/sector-groups', {});
  });

  it('should updateSectorGroup', () => {
    service.updateSectorGroup(123, {} as ReadSectorGroupVersion);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/sector-groups/123', {});
  });
});
