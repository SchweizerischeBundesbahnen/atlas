import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { TransportCompanyRelationInternalService } from './transport-company-relation-internal.service';
import { TransportCompanyRelation } from '../../model/transportCompanyRelation';
import { UpdateTransportCompanyRelation } from '../../model/updateTransportCompanyRelation';
import { UserService } from '../../../core/auth/user/user.service';

describe('TransportCompanyRelationInternalService', () => {
  let service: TransportCompanyRelationInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TransportCompanyRelationInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TransportCompanyRelationInternalService);
    apiService = TestBed.inject(AtlasApiService);

    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'post');
    spyOn(apiService, 'delete');
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
  });

  it('should createTransportCompanyRelation', () => {
    const transportCompanyRelation: TransportCompanyRelation = {} as TransportCompanyRelation;

    service.createTransportCompanyRelation(transportCompanyRelation);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ transportCompanyRelation });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/transport-company-relations',
      transportCompanyRelation,
    );
  });

  it('should deleteTransportCompanyRelation', () => {
    const relationId = 101;

    service.deleteTransportCompanyRelation(relationId);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ relationId });
    expect(apiService.delete).toHaveBeenCalledOnceWith(
      `/business-organisation-directory/internal/transport-company-relations/${relationId}`,
    );
  });

  it('should getTransportCompanyRelations', () => {
    const transportCompanyId = 202;

    service.getTransportCompanyRelations(transportCompanyId);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ transportCompanyId });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      `/business-organisation-directory/internal/transport-company-relations/${transportCompanyId}`,
    );
  });

  it('should updateTransportCompanyRelation', () => {
    const updateTransportCompanyRelation: UpdateTransportCompanyRelation = {} as UpdateTransportCompanyRelation;

    service.updateTransportCompanyRelation(updateTransportCompanyRelation);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ updateTransportCompanyRelation });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/business-organisation-directory/internal/transport-company-relations',
      updateTransportCompanyRelation,
    );
  });
});
