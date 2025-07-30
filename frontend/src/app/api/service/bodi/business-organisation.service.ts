import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { BusinessOrganisationVersion } from '../../model/businessOrganisationVersion';

@Injectable({
  providedIn: 'root',
})
export class BusinessOrganisationService {

  private readonly BASE_PATH = '/business-organisation-directory/v1/business-organisations';

  private readonly atlasApiService = inject(AtlasApiService);

  getVersions(sboid: string): Observable<BusinessOrganisationVersion[]> {
    this.atlasApiService.validateParams({ sboid });
    return this.atlasApiService.get(`${this.BASE_PATH}/versions/${encodeURIComponent(sboid)}`);
  }
}
