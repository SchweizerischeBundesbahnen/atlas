import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AtlasApiService } from '../atlas-api.service';
import { TransportCompanyRelationInternalService } from './transport-company-relation-internal.service';
import { TransportCompanyRelation } from '../../model/transportCompanyRelation';
import { UpdateTransportCompanyRelation } from '../../model/updateTransportCompanyRelation';
import { UserService } from '../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

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

    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'delete').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should createTransportCompanyRelation', () => {
    const transportCompanyRelation: TransportCompanyRelation = {} as TransportCompanyRelation;

    service.createTransportCompanyRelation(transportCompanyRelation);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ transportCompanyRelation });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/business-organisation-directory/internal/transport-company-relations',
      transportCompanyRelation
    );
  });

  it('should deleteTransportCompanyRelation', () => {
    const relationId = 101;

    service.deleteTransportCompanyRelation(relationId);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ relationId });
    expect(apiService.delete).toHaveBeenCalledExactlyOnceWith(
      `/business-organisation-directory/internal/transport-company-relations/${relationId}`
    );
  });

  it('should getTransportCompanyBoRelations', () => {
    const transportCompanyId = 202;

    service.getTransportCompanyBoRelations(transportCompanyId);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ transportCompanyId });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      `/business-organisation-directory/internal/transport-company-relations/${transportCompanyId}`
    );
  });

  it('should getBoTransportCompanyRelations', () => {
    const sboid = 'ch:1:sboid:100';

    service.getBoTransportCompanyRelations(sboid);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ sboid });
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({ sboid });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/business-organisation-directory/internal/transport-company-relations/tc-of-bo',
      expect.any(HttpParams)
    );
  });

  it('should updateTransportCompanyRelation', () => {
    const updateTransportCompanyRelation: UpdateTransportCompanyRelation = {} as UpdateTransportCompanyRelation;

    service.updateTransportCompanyRelation(updateTransportCompanyRelation);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ updateTransportCompanyRelation });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/business-organisation-directory/internal/transport-company-relations',
      updateTransportCompanyRelation
    );
  });
});
