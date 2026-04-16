import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, Observable, of } from 'rxjs';
import { BusinessOrganisationVersion, Status } from '../../../../api';
import {
  BusinessOrganisationDetailResolver,
  businessOrganisationResolver,
} from './business-organisation-detail-resolver.service';
import { AppTestingModule } from '../../../../app.testing.module';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const version: BusinessOrganisationVersion = {
  id: 1234,
  organisationNumber: 1234,
  sboid: 'sboid',
  descriptionDe: 'asdf',
  descriptionFr: 'asdf',
  descriptionIt: 'asdf',
  descriptionEn: 'asdf',
  abbreviationDe: 'asdf',
  abbreviationFr: 'asdf',
  abbreviationIt: 'asdf',
  abbreviationEn: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

describe('BusinessOrganisationDetailResolver', () => {
  let resolver: BusinessOrganisationDetailResolver;
  let businessOrganisationsService: Mocked<Pick<BusinessOrganisationService, 'getVersions'>>;

  beforeEach(() => {
    businessOrganisationsService = {
      getVersions: vi.fn(),
    };
    businessOrganisationsService.getVersions.mockReturnValue(of([version]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        BusinessOrganisationDetailResolver,
        {
          provide: BusinessOrganisationService,
          useValue: businessOrganisationsService,
        },
      ],
    });

    resolver = TestBed.inject(BusinessOrganisationDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      businessOrganisationResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<BusinessOrganisationVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(1);
    expect(versions[0].id).toBe(1234);
    expect(versions[0].status).toBe(Status.Validated);
    expect(versions[0].sboid).toBe('sboid');
  });
});
