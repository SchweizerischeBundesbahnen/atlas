import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ApplicationType } from '../../model/applicationType';
import { Permission } from '../../model/permission';
import { ClientCredential } from '../../model/clientCredential';
import { ContainerClientCredential } from '../../model/containerClientCredential';
import { ClientCredentialCreate } from '../../model/clientCredentialCreate';

@Injectable({
  providedIn: 'root',
})
export class ClientCredentialAdministrationService {

  private readonly CLIENT_CREDENTIAL_BASE_PATH = '/user-administration/v1/client-credentials';

  private readonly atlasApiService = inject(AtlasApiService);

  getClientCredentials(
    page: number,
    size: number,
    sort?: Array<string>
  ): Observable<ContainerClientCredential> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.CLIENT_CREDENTIAL_BASE_PATH, httpParams);
  }

  getClientCredential(clientId: string): Observable<ClientCredential> {
    return this.atlasApiService.get(`${this.CLIENT_CREDENTIAL_BASE_PATH}/${clientId}`);
  }

  createClientCredential(clientCredential: ClientCredentialCreate): Observable<ClientCredential> {
    return this.atlasApiService.post(this.CLIENT_CREDENTIAL_BASE_PATH, clientCredential);
  }

  updateClientCredentialPermissions(clientId: string, application: ApplicationType, permission: Permission,
  ): Observable<ClientCredential> {
    return this.atlasApiService.put(
      `${this.CLIENT_CREDENTIAL_BASE_PATH}/${clientId}/${application}`, permission);
  }

}
