import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api';
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
  businessOrganisation: 'SBB',
  paymentType: PaymentTypeEnum.None,
  swissSublineNumber: 'L1:2',
  type: TypeEnum.Technical,
  mainlineSlnid: 'ch:1:slnid:1000',
};

describe('SublineDetailResolver', () => {
  const sublinesServiceSpy = jasmine.createSpyObj('sublinesService', ['getSubline']);
  sublinesServiceSpy.getSubline.and.returnValue(of([version]));

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

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(1234);
      expect(versions[0].status).toBe(StatusEnum.Active);
      expect(versions[0].slnid).toBe('slnid');
    });
  });
});
