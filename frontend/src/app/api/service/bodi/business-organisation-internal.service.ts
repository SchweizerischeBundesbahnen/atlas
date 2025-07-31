import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { BusinessOrganisationVersion } from '../../model/businessOrganisationVersion';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BusinessOrganisationInternalService {

  private readonly BASE_PATH = '/business-organisation-directory/internal/business-organisations';

  private readonly atlasApiService = inject(AtlasApiService);

  createBusinessOrganisationVersion(businessOrganisationVersion: BusinessOrganisationVersion): Observable<BusinessOrganisationVersion> {
    this.atlasApiService.validateParams({businessOrganisationVersion});
    return this.atlasApiService.post(`${this.BASE_PATH}/versions`, businessOrganisationVersion);
  }

  deleteBusinessOrganisation(sboid: string): Observable<void> {
    this.atlasApiService.validateParams({sboid});
    return this.atlasApiService.delete(`${this.BASE_PATH}/${encodeURIComponent(sboid)}`);
  }

  revokeBusinessOrganisation(sboid: string): Observable<BusinessOrganisationVersion[]> {
    this.atlasApiService.validateParams({sboid});
    return this.atlasApiService.post(`${this.BASE_PATH}/${encodeURIComponent(sboid)}/revoke`);
  }

  updateBusinessOrganisationVersion(id: number, businessOrganisationVersion: BusinessOrganisationVersion): Observable<BusinessOrganisationVersion[]> {
    this.atlasApiService.validateParams({id, businessOrganisationVersion});
    return this.atlasApiService.put(`${this.BASE_PATH}/versions/${id}`, businessOrganisationVersion);
  }

}
