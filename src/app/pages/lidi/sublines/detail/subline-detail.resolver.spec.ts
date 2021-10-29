import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api/lidi';
import { SublineDetailResolver } from './subline-detail-resolver.service';
import StatusEnum = SublineVersion.StatusEnum;
import PaymentTypeEnum = SublineVersion.PaymentTypeEnum;
import TypeEnum = SublineVersion.TypeEnum;

const version: SublineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: '',
  paymentType: PaymentTypeEnum.None,
  swissSublineNumber: '',
  type: TypeEnum.Technical,
};

describe('SublineDetailResolver', () => {
  const sublinesServiceSpy = jasmine.createSpyObj('sublinesService', ['getSublineVersion']);
  sublinesServiceSpy.getSublineVersion.and.returnValue(of(version));

  let resolver: SublineDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        SublineDetailResolver,
        { provide: SublinesService, useValue: sublinesServiceSpy },
      ],
    });
    resolver = TestBed.inject(SublineDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((version) => {
      expect(version.id).toBe(1234);
      expect(version.status).toBe(StatusEnum.Active);
      expect(version.slnid).toBe('slnid');
    });
  });
});
