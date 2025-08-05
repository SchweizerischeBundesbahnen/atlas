import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { TransportCompany } from '../../model/transportCompany';

@Injectable({
  providedIn: 'root',
})
export class TransportCompanyInternalService {

  private readonly BASE_PATH = '/business-organisation-directory/internal/transport-companies';

  private readonly atlasApiService = inject(AtlasApiService);

  getTransportCompany(id: number): Observable<TransportCompany> {
    this.atlasApiService.validateParams({ id });
    return this.atlasApiService.get(`${this.BASE_PATH}/${id}`);
  }

}
