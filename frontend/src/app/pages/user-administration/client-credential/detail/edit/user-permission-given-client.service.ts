import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { Injectable } from '@angular/core';
import { ApplicationType, ClientCredential } from '../../../../../api';

@Injectable({
  providedIn: 'root',
})
export class UserPermissionGivenClientService extends UserPermissionProviderService {
  clientCredential!: ClientCredential;

  showAllSpecialPermissions(): boolean {
    return true;
  }

  loadForm(application: ApplicationType): void {
    this.setPermissionForm(application, this.getPermission(this.clientCredential.permissions ?? [], application));
  }
}
