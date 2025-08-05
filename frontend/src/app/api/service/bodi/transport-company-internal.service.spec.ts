import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { TransportCompanyInternalService } from './transport-company-internal.service';
import { UserService } from '../../../core/auth/user/user.service';

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

    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
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
