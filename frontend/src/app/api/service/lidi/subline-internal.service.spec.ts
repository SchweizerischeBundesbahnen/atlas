import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SublineInternalService } from './subline-internal.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('SublineInternalService', () => {
  let service: SublineInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SublineInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(SublineInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'delete').mockImplementation(() => EMPTY);
  });

  it('should revokeSubline', () => {
    service.revokeSubline('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      slnid: '123',
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/sublines/123/revoke');
  });

  it('should deleteSublines', () => {
    service.deleteSublines('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      slnid: '123',
    });
    expect(apiService.delete).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/sublines/123');
  });
});
