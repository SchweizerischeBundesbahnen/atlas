import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { BusinessOrganisationsService, BusinessOrganisationVersion, Status } from '../../../../api';
import { BusinessOrganisationDetailResolver } from './business-organisation-detail-resolver.service';
import { AppTestingModule } from '../../../../app.testing.module';

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
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

describe('BusinessOrganisationDetailResolver', () => {
  const businessOrganisationsServiceSpy = jasmine.createSpyObj('businessOrganisationsService', [
    'getBusinessOrganisationVersions',
  ]);
  businessOrganisationsServiceSpy.getBusinessOrganisationVersions.and.returnValue(of([version]));

  let resolver: BusinessOrganisationDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        BusinessOrganisationDetailResolver,
        { provide: BusinessOrganisationsService, useValue: businessOrganisationsServiceSpy },
      ],
    });
    resolver = TestBed.inject(BusinessOrganisationDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(1234);
      expect(versions[0].status).toBe(Status.Active);
      expect(versions[0].sboid).toBe('sboid');
    });
  });
});
