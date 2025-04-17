import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { CompaniesService, Company } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { CompanyDetailResolver } from './company-detail-resolver.service';

const company: Company = {
  uicCode: 1234,
  name: 'SBB',
};

describe('CompanyDetailResolver', () => {
  const companyService = jasmine.createSpyObj('companyService', ['getCompany']);
  companyService.getCompany.and.returnValue(of(company));

  let resolver: CompanyDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        CompanyDetailResolver,
        { provide: CompaniesService, useValue: companyService },
      ],
    });
    resolver = TestBed.inject(CompanyDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get company from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((tranyportCompany) => {
      expect(tranyportCompany.uicCode).toBe(1234);
      expect(tranyportCompany.name).toBe('SBB');
    });
  });
});
