import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { Injectable } from '@angular/core';
import { ApplicationType, Permission, User } from '../../../../../api';

@Injectable({
  providedIn: 'root',
})
export class UserPermissionGivenUserService extends UserPermissionProviderService {
  user!: User;
  showAllSpecialPermissions(): boolean {
    return true;
  }

  loadForm(application: ApplicationType): void {
    const permission: Permission = super.getPermission(this.user.permissions, application);
    this.setPermissionForm(application, permission);
  }
}
