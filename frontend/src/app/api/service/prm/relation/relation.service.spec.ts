import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RelationService } from './relation.service';
import { AtlasApiService } from '../../atlas-api.service';
import { StopPointService } from '../stop-point/stop-point.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from '../../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

describe('RelationService', () => {
  let service: RelationService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StopPointService,
        AtlasApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(RelationService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getRelationsBySloid', () => {
    service.getRelationsBySloid('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: 'ch:1:sloid:7000',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/v1/relations/ch:1:sloid:7000');
  });
});
