import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { TransportCompany, TransportCompanyBoRelation } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { TransportCompanyDetailResolver } from './transport-company-detail.resolver';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { TransportCompanyService } from '../../../../api/service/bodi/transport-company.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const transportCompany: TransportCompany = {
  id: 1234,
  number: '#001',
  description: 'SBB',
};

const transportCompanyRelations: TransportCompanyBoRelation[] = [{ id: 1 }, { id: 2 }];

describe('TransportCompanyDetailResolver', () => {
  let resolver: TransportCompanyDetailResolver;
  let transportCompanyService: Mocked<Pick<TransportCompanyService, 'getTransportCompany'>>;
  let transportCompanyRelationInternalService: Mocked<
    Pick<TransportCompanyRelationInternalService, 'getTransportCompanyBoRelations'>
  >;

  beforeEach(() => {
    transportCompanyService = {
      getTransportCompany: vi.fn(),
    };
    transportCompanyService.getTransportCompany.mockReturnValue(of(transportCompany));

    transportCompanyRelationInternalService = {
      getTransportCompanyBoRelations: vi.fn(),
    };
    transportCompanyRelationInternalService.getTransportCompanyBoRelations.mockReturnValue(
      of(transportCompanyRelations)
    );

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        TransportCompanyDetailResolver,
        { provide: TransportCompanyService, useValue: transportCompanyService },
        {
          provide: TransportCompanyRelationInternalService,
          useValue: transportCompanyRelationInternalService,
        },
      ],
    });

    resolver = TestBed.inject(TransportCompanyDetailResolver);
  });

  it('should get transportCompany and transportCompanyRelations from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    const [transportCompany, transportCompanyRelations] = await firstValueFrom(resolvedVersion);
    expect(transportCompany.id).toBe(1234);
    expect(transportCompany.description).toBe('SBB');
    expect(transportCompanyRelations.length).toBe(2);
    expect(transportCompanyRelations[0].id).toBe(1);
    expect(transportCompanyRelations[1].id).toBe(2);
  });
});
