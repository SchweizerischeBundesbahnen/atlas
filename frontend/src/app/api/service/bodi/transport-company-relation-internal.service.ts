import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { TransportCompanyBoRelation } from '../../model/transportCompanyBoRelation';
import { TransportCompanyRelation } from '../../model/transportCompanyRelation';
import { UpdateTransportCompanyRelation } from '../../model/updateTransportCompanyRelation';

@Injectable({
  providedIn: 'root',
})
export class TransportCompanyRelationInternalService {

  private readonly BASE_PATH = '/business-organisation-directory/internal/transport-company-relations';

  private readonly atlasApiService = inject(AtlasApiService);

  createTransportCompanyRelation(transportCompanyRelation: TransportCompanyRelation): Observable<TransportCompanyBoRelation> {
    this.atlasApiService.validateParams({ transportCompanyRelation });
    return this.atlasApiService.post(this.BASE_PATH, transportCompanyRelation);
  }

  deleteTransportCompanyRelation(relationId: number): Observable<void> {
    this.atlasApiService.validateParams({ relationId });
    return this.atlasApiService.delete(`${this.BASE_PATH}/${relationId}`);
  }

  getTransportCompanyRelations(transportCompanyId: number): Observable<TransportCompanyBoRelation[]> {
    this.atlasApiService.validateParams({ transportCompanyId });
    return this.atlasApiService.get(`${this.BASE_PATH}/${transportCompanyId}`);
  }

  updateTransportCompanyRelation(updateTransportCompanyRelation: UpdateTransportCompanyRelation): Observable<void> {
    this.atlasApiService.validateParams({ updateTransportCompanyRelation });
    return this.atlasApiService.put(this.BASE_PATH, updateTransportCompanyRelation);
  }
}
// todo: tests, remove comments in api.ts and remove unused services in /api
