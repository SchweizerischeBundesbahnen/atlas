import {TestBed} from '@angular/core/testing';
import {AtlasApiService} from '../atlas-api.service';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../../../core/auth/user/user.service';
import {SectorInternalService} from './sector-internal.service';
import {SectorService} from './sector.service';
import {CreateSectorVersion} from '../../model/createSectorVersion';

describe('SectorService', () => {
  let service: SectorService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SectorInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(SectorService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getSector', () => {
    service.getSector('ch:1:sloid:7000:1');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/sectors/ch%3A1%3Asloid%3A7000%3A1');
  });

  it('should createSector', () => {
    service.createSector( {} as CreateSectorVersion);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/sectors',
      {}
    );
  });

  it('should updateSector', () => {
    service.updateSector(123, {} as CreateSectorVersion);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/sectors/123',
      {}
    );
  });
});
