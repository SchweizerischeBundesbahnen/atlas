import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { TransportCompany, TransportCompanyBoRelation } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { TransportCompanyDetailResolver } from './transport-company-detail-resolver.service';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { TransportCompanyService } from '../../../../api/service/bodi/transport-company.service';
import SpyObj = jasmine.SpyObj;

const transportCompany: TransportCompany = {
  id: 1234,
  number: '#001',
  description: 'SBB',
};

const transportCompanyRelations: TransportCompanyBoRelation[] = [
  {
    id: 1,
  },
  {
    id: 2,
  },
];

describe('TransportCompanyDetailResolver', () => {
  let resolver: TransportCompanyDetailResolver;

  let transportCompanyServiceSpy: SpyObj<TransportCompanyService>;
  let transportCompanyRelationInternalServiceSpy: SpyObj<TransportCompanyRelationInternalService>;

  beforeEach(() => {
    transportCompanyServiceSpy = jasmine.createSpyObj({
      getTransportCompany: of(transportCompany),
    });
    transportCompanyRelationInternalServiceSpy = jasmine.createSpyObj({
      getTransportCompanyRelations: of(transportCompanyRelations),
    });

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        TransportCompanyDetailResolver,
        {
          provide: TransportCompanyService,
          useValue: transportCompanyServiceSpy,
        },
        {
          provide: TransportCompanyRelationInternalService,
          useValue: transportCompanyRelationInternalServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(TransportCompanyDetailResolver);
  });

  it('should get transportCompany and transportCompanyRelations from service to display', (done) => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe(
      ([transportCompany, transportCompanyRelations]) => {
        expect(transportCompany.id).toBe(1234);
        expect(transportCompany.description).toBe('SBB');
        expect(transportCompanyRelations.length).toBe(2);
        expect(transportCompanyRelations[0].id).toBe(1);
        expect(transportCompanyRelations[1].id).toBe(2);
        done();
      }
    );
  });
});
