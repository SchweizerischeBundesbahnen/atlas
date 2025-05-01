import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SublineService } from './subline.service';
import { CreateSublineVersionV2 } from '../../model/createSublineVersionV2';
import { SublineVersionV2 } from '../../model/sublineVersionV2';

describe('SublineService', () => {
  let service: SublineService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SublineService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(SublineService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'post');
    spyOn(apiService, 'put');
  });

  it('should getSublineVersionV2', () => {
    service.getSublineVersionV2('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      slnid: '123',
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/v2/sublines/versions/123',
    );
  });

  it('should createSublineVersionV2', () => {
    service.createSublineVersionV2({} as CreateSublineVersionV2);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      createSublineVersionV2: {},
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/v2/sublines/versions',
      {}
    );
  });

  it('should updateSublineVersionV2', () => {
    service.updateSublineVersionV2(1, {} as SublineVersionV2);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
      sublineVersionV2: {},
    });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/line-directory/v2/sublines/versions/1',
      {}
    );
  });
});
