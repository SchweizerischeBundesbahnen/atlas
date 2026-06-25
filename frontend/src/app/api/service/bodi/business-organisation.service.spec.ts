import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { BusinessOrganisationService } from './business-organisation.service';
import { UserService } from '../../../core/auth/user/user.service';
import { Status } from '../../model/status';
import { EMPTY } from 'rxjs';

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

    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getVersions', () => {
    const sboid = 'test-id';

    service.getVersions(sboid);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ sboid });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/business-organisation-directory/v1/business-organisations/versions/' + encodeURIComponent(sboid)
    );
  });

  it('should getAllBusinessOrganisations', () => {
    const searchCriteria = ['criteria1', 'criteria2'];
    const inSboids = ['sboid1', 'sboid2'];
    const validOn = new Date('2025-01-01');
    const statusChoices: Status[] = ['VALIDATED', 'IN_REVIEW'];
    const page = 1;
    const size = 20;
    const sort = ['name,asc'];

    service.getAllBusinessOrganisations(searchCriteria, inSboids, validOn, statusChoices, page, size, sort);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      searchCriteria,
      inSboids,
      validOn,
      statusChoices,
      page,
      size,
      sort,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/business-organisation-directory/v1/business-organisations',
      expect.any(HttpParams)
    );
  });
});
