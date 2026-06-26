import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SectorInternalService } from './sector-internal.service';

describe('SectorInternalService', () => {
  let service: SectorInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SectorInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(SectorInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should getSectors', () => {
    service.getSectors('ch:1:sloid:7000:1');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/sectors/ch%3A1%3Asloid%3A7000%3A1/overview',
      expect.any(HttpParams)
    );
  });

  it('should revoke sector', () => {
    service.revokeSector('ch:1:sloid:7000:1');

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/service-point-directory/internal/sectors/ch%3A1%3Asloid%3A7000%3A1/revoke'
    );
  });
});
