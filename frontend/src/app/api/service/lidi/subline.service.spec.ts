import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SublineService } from './subline.service';
import { CreateSublineVersionV2 } from '../../model/createSublineVersionV2';
import { SublineVersionV2 } from '../../model/sublineVersionV2';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('SublineService', () => {
  let service: SublineService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SublineService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(SublineService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should getSublineVersionV2', () => {
    service.getSublineVersionV2('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      slnid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/v2/sublines/versions/123');
  });

  it('should createSublineVersionV2', () => {
    service.createSublineVersionV2({} as CreateSublineVersionV2);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      createSublineVersionV2: {},
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/v2/sublines/versions', {});
  });

  it('should updateSublineVersionV2', () => {
    service.updateSublineVersionV2(1, {} as SublineVersionV2);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
      sublineVersionV2: {},
    });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/line-directory/v2/sublines/versions/1', {});
  });
});
