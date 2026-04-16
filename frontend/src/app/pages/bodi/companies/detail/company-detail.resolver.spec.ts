import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { Company } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { CompanyDetailResolver } from './company-detail-resolver.service';
import { CompanyService } from '../../../../api/service/bodi/company.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const company: Company = {
  uicCode: '1234',
  name: 'SBB',
};

describe('CompanyDetailResolver', () => {
  let resolver: CompanyDetailResolver;
  let companyService: Mocked<Pick<CompanyService, 'getCompany'>>;

  beforeEach(() => {
    companyService = {
      getCompany: vi.fn(),
    };
    companyService.getCompany.mockReturnValue(of(company));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [CompanyDetailResolver, { provide: CompanyService, useValue: companyService }],
    });

    resolver = TestBed.inject(CompanyDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get company from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    const transportCompany = await firstValueFrom(resolvedVersion);
    expect(transportCompany.uicCode).toBe('1234');
    expect(transportCompany.name).toBe('SBB');
  });
});
