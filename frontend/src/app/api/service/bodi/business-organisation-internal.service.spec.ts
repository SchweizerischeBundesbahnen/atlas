import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { BusinessOrganisationInternalService } from './business-organisation-internal.service';
import { BusinessOrganisationVersion } from '../../model/businessOrganisationVersion';
import { UserService } from '../../../core/auth/user/user.service';

describe('BusinessOrganisationInternalService', () => {
  let service: BusinessOrganisationInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BusinessOrganisationInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(BusinessOrganisationInternalService);
    apiService = TestBed.inject(AtlasApiService);

    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'post');
    spyOn(apiService, 'delete');
    spyOn(apiService, 'put');
  });

  it('should createBusinessOrganisationVersion', () => {
    const version: BusinessOrganisationVersion = {} as BusinessOrganisationVersion;

    service.createBusinessOrganisationVersion(version);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      businessOrganisationVersion: version,
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/business-organisations/versions',
      version
    );
  });

  it('should deleteBusinessOrganisation', () => {
    const sboid = '123-abc';

    service.deleteBusinessOrganisation(sboid);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sboid,
    });
    expect(apiService.delete).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/business-organisations/' + encodeURIComponent(sboid)
    );
  });

  it('should revokeBusinessOrganisation', () => {
    const sboid = '456-def';

    service.revokeBusinessOrganisation(sboid);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sboid,
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/business-organisations/' + encodeURIComponent(sboid) + '/revoke'
    );
  });

  it('should updateBusinessOrganisationVersion', () => {
    const id = 42;
    const version: BusinessOrganisationVersion = {} as BusinessOrganisationVersion;

    service.updateBusinessOrganisationVersion(id, version);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id,
      businessOrganisationVersion: version,
    });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/business-organisations/versions/' + id,
      version
    );
  });
});
