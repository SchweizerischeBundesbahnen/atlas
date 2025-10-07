import { TestBed } from '@angular/core/testing';

import { SectorGroupService } from './sector-group.service';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { CreateSectorGroupVersion } from '../../model/createSectorGroupVersion';
import { SectorGroupVersion } from '../../model/sectorGroupVersion';

describe('SectorGroupService', () => {
  let service: SectorGroupService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AtlasApiService,
        { provide: HttpClient, useValue: {}},
        { provide: UserService, useValue: {}},
      ],
    });
    service = TestBed.inject(SectorGroupService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getSectorGroups', () => {
    service.getSectorGroup('ch:1:sloid:7000:1');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/sector-groups/ch%3A1%3Asloid%3A7000%3A1');
  });

  it('should createSectorGroup', () => {
    service.createSectorGroup( {} as CreateSectorGroupVersion);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/sector-groups',
      {}
    );
  });

  it('should updateSectorGroup', () => {
    service.updateSectorGroup(123, {} as SectorGroupVersion);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/sector-groups/123',
      {}
    );
  });
});
