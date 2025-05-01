import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SublineInternalService } from './subline-internal.service';

describe('SublineInternalService', () => {
  let service: SublineInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SublineInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(SublineInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'post');
    spyOn(apiService, 'delete');
  });

  it('should revokeSubline', () => {
    service.revokeSubline('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      slnid: '123',
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/sublines/123/revoke',
    );
  });

  it('should deleteSublines', () => {
    service.deleteSublines('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      slnid: '123',
    });
    expect(apiService.delete).toHaveBeenCalledOnceWith(
      '/line-directory/internal/sublines/123',
    );
  });
});
