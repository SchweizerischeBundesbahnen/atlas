import { LocationService } from './location.service';
import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { UserService } from '../../../core/auth/user/user.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('LocationService', () => {
  let service: LocationService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LocationService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(LocationService);
    apiService = TestBed.inject(AtlasApiService);

    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getSloidLocationModel', () => {
    const sloid = 'ch:1:slnid:123';
    service.getSloidLocationModel(sloid);

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(`/location/v1/sloid/${encodeURIComponent(sloid)}`);
  });
});
