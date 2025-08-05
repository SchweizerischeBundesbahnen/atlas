import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { TransportCompanyStatus } from '../../model/transportCompanyStatus';
import { Observable } from 'rxjs';
import { ContainerTransportCompany } from '../../model/containerTransportCompany';

@Injectable({
  providedIn: 'root',
})
export class TransportCompanyService {

  private readonly BASE_PATH = '/business-organisation-directory/v1/transport-companies';

  private readonly atlasApiService = inject(AtlasApiService);

  getTransportCompanies(searchCriteria?: Array<string>, statusChoices?: Array<TransportCompanyStatus>, page?: number, size?: number, sort?: Array<string>): Observable<ContainerTransportCompany> {
    const httpParams = this.atlasApiService.paramsOf({
      searchCriteria,
      statusChoices,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.BASE_PATH, httpParams);
  }

}
