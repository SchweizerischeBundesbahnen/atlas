import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { CompanyInternalService } from './company-internal.service';
import { UserService } from '../../../core/auth/user/user.service';

describe('CompanyInternalService', () => {
  let service: CompanyInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CompanyInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(CompanyInternalService);
    apiService = TestBed.inject(AtlasApiService);

    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getCompanies', () => {
    const searchCriteria = ['criteria1', 'criteria2'];
    const page = 1;
    const size = 50;
    const sort = ['name,asc'];

    service.getCompanies(searchCriteria, page, size, sort);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      searchCriteria,
      page,
      size,
      sort,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/companies',
      jasmine.any(HttpParams),
    );
  });

  it('should getCompany', () => {
    const uic = 123456;

    service.getCompany(uic);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ uic });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      `/business-organisation-directory/internal/companies/${uic}`,
    );
  });
});
