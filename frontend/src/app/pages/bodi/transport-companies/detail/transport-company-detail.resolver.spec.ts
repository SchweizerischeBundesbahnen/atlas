import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { TransportCompaniesService, TransportCompany } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { TransportCompanyDetailResolver } from './transport-company-detail-resolver.service';

const transportCompany: TransportCompany = {
  id: 1234,
  number: '#001',
  description: 'SBB',
};

describe('BusinessOrganisationDetailResolver', () => {
  const transportCompanyService = jasmine.createSpyObj('transportCompanyService', [
    'getTransportCompany',
  ]);
  transportCompanyService.getTransportCompany.and.returnValue(of(transportCompany));

  let resolver: TransportCompanyDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        TransportCompanyDetailResolver,
        { provide: TransportCompaniesService, useValue: transportCompanyService },
      ],
    });
    resolver = TestBed.inject(TransportCompanyDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get transportCompany from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((tranyportCompany) => {
      expect(tranyportCompany.id).toBe(1234);
      expect(tranyportCompany.description).toBe('SBB');
    });
  });
});
