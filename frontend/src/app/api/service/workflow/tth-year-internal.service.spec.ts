import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TthYearInternalService } from './tth-year-internal.service';

describe('TthYearInternalService', () => {
  let service: TthYearInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TthYearInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(TthYearInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'validateParams');
  });

  it('should closeTimetableHearing', () => {
    service.closeTimetableHearingYear(2025);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      year: 2025,
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/year/2025/close');
  });
});
