import { inject } from '@angular/core';
import { UserService } from '../../core/auth/user/user.service';
import { ApplicationType, Permission } from '../../api';
import { UserPermissionProviderService } from '../../core/components/permissions/application-permission/user-permission-provider-service';

export class UserPermissionCurrentUserService extends UserPermissionProviderService {
  private readonly userService = inject(UserService);
  showAllSpecialPermissions(): boolean {
    return false;
  }

  loadForm(application: ApplicationType): void {
    const permission: Permission = super.getPermission(this.userService.currentUser!.permissions, application);
    this.setPermissionForm(application, permission);
  }
}
