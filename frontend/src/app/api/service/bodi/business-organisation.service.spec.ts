import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { BusinessOrganisationService } from './business-organisation.service';
import { UserService } from '../../../core/auth/user/user.service';

describe('BusinessOrganisationService', () => {
  let service: BusinessOrganisationService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BusinessOrganisationService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(BusinessOrganisationService);
    apiService = TestBed.inject(AtlasApiService);

    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getVersions', () => {
    const sboid = 'test-id';

    service.getVersions(sboid);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ sboid });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/v1/business-organisations/versions/' + encodeURIComponent(sboid),
    );
  });
});
