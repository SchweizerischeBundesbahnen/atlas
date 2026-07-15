import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { CompanyService } from './company.service';
import { UserService } from '../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

describe('CompanyService', () => {
  let service: CompanyService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CompanyService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(CompanyService);
    apiService = TestBed.inject(AtlasApiService);

    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
  });

  it('should getCompanies', () => {
    const searchCriteria = ['criteria1', 'criteria2'];
    const page = 1;
    const size = 50;
    const sort = ['name,asc'];

    service.getCompanies(searchCriteria, page, size, sort);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      searchCriteria,
      page,
      size,
      sort,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/business-organisation-directory/v1/companies',
      expect.any(HttpParams)
    );
  });

  it('should getCompany', () => {
    const uic = '123456';

    service.getCompany(uic);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ uic });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(`/business-organisation-directory/v1/companies/${uic}`);
  });
});
