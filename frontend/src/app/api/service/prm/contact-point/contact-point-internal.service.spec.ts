import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ContactPointInternalService } from './contact-point-internal.service';
import { AtlasApiService } from '../../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

describe('ContactPointInternalService', () => {
  let service: ContactPointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ContactPointInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(ContactPointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getContactPointOverview', () => {
    service.getContactPointOverview('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      parentServicePointSloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/prm-directory/internal/contact-points/overview/123');
  });
});
