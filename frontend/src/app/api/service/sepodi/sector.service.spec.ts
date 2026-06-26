import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SectorInternalService } from './sector-internal.service';
import { SectorService } from './sector.service';
import { CreateSectorVersion } from '../../model/createSectorVersion';

describe('SectorService', () => {
  let service: SectorService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SectorInternalService,
        SectorService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(SectorService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getSector', () => {
    service.getSector('ch:1:sloid:7000:1');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/v1/sectors/ch%3A1%3Asloid%3A7000%3A1'
    );
  });

  it('should createSector', () => {
    service.createSector({} as CreateSectorVersion);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/sectors', {});
  });

  it('should updateSector', () => {
    service.updateSector(123, {} as CreateSectorVersion);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/service-point-directory/v1/sectors/123', {});
  });
});
