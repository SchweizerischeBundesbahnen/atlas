import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { LinesService, LineType, LineVersion, PaymentType, Status } from '../../../../api';
import { BusinessOrganisationDetailResolver } from './business-organisation-detail-resolver.service';
import { AppTestingModule } from '../../../../app.testing.module';

const version: LineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: Status.Active,
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  paymentType: PaymentType.None,
  swissLineNumber: 'L1',
  lineType: LineType.Orderly,
  colorBackCmyk: '',
  colorBackRgb: '',
  colorFontCmyk: '',
  colorFontRgb: '',
};

describe('BusinessOrganisationDetailResolver', () => {
  const linesServiceSpy = jasmine.createSpyObj('linesService', ['getLineVersions']);
  linesServiceSpy.getLineVersions.and.returnValue(of([version]));

  let resolver: BusinessOrganisationDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        BusinessOrganisationDetailResolver,
        { provide: LinesService, useValue: linesServiceSpy },
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
      expect(versions[0].slnid).toBe('slnid');
    });
  });
});
