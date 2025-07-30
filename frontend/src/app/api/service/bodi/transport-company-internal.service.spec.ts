import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { TransportCompanyInternalService } from './transport-company-internal.service';
import { TransportCompanyStatus } from '../../model/transportCompanyStatus';
import { UserService } from '../../../core/auth/user/user.service';
import any = jasmine.any;

describe('TransportCompanyInternalService', () => {
  let service: TransportCompanyInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TransportCompanyInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TransportCompanyInternalService);
    apiService = TestBed.inject(AtlasApiService);

    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getTransportCompanies', () => {
    const searchCriteria = ['crit1', 'crit2'];
    const statusChoices: TransportCompanyStatus[] = ['OPERATOR', 'CURRENT'];
    const page = 2;
    const size = 10;
    const sort = ['name,desc'];

    service.getTransportCompanies(searchCriteria, statusChoices, page, size, sort);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      searchCriteria,
      statusChoices,
      page,
      size,
      sort,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/transport-companies',
      any(HttpParams),
    );
  });

  it('should getTransportCompany', () => {
    const id = 123;

    service.getTransportCompany(id);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ id });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      `/business-organisation-directory/internal/transport-companies/${id}`,
    );
  });
});
