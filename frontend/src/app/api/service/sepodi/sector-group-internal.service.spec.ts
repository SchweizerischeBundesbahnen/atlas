import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { SectorGroupInternalService } from './sector-group-internal.service';

describe('SectorGroupInternalService', () => {
  let service: SectorGroupInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SectorGroupInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(SectorGroupInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getSectorGroups', () => {
    service.getSectorGroups('ch:1:sloid:7000:1');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/internal/sector-groups/ch%3A1%3Asloid%3A7000%3A1/overview', jasmine.any(HttpParams));
  });

});
