import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { Observable, of } from 'rxjs';
import {
  TransportCompaniesService,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyRelationsService,
} from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { TransportCompanyDetailResolver } from './transport-company-detail-resolver.service';

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

type getTransportCompanyRelationsType = (
  transportCompanyId: number,
  observe?: 'body',
  reportProgress?: boolean,
  options?: { httpHeaderAccept?: '*/*' }
) => Observable<TransportCompanyBoRelation[]>;

describe('TransportCompanyDetailResolver', () => {
  const transportCompanyService = jasmine.createSpyObj(
    'transportCompanyService',
    ['getTransportCompany']
  );
  const getTransportCompanyRelationsSpy =
    jasmine.createSpy<getTransportCompanyRelationsType>(
      'getTransportCompanyRelations',
      TransportCompanyRelationsService.prototype.getTransportCompanyRelations
    );

  transportCompanyService.getTransportCompany.and.returnValue(
    of(transportCompany)
  );
  getTransportCompanyRelationsSpy.and.returnValue(
    of(transportCompanyRelations)
  );

  let resolver: TransportCompanyDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        TransportCompanyDetailResolver,
        {
          provide: TransportCompaniesService,
          useValue: transportCompanyService,
        },
        {
          provide: TransportCompanyRelationsService,
          useValue: {
            getTransportCompanyRelations: getTransportCompanyRelationsSpy,
          },
        },
      ],
    });
    resolver = TestBed.inject(TransportCompanyDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get transportCompany and transportCompanyRelations from service to display', (done) => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe(
      ([tranyportCompany, transportCompanyRelations]) => {
        expect(tranyportCompany.id).toBe(1234);
        expect(tranyportCompany.description).toBe('SBB');
        expect(transportCompanyRelations.length).toBe(2);
        expect(transportCompanyRelations[0].id).toBe(1);
        expect(transportCompanyRelations[1].id).toBe(2);
        done();
      }
    );
  });
});
