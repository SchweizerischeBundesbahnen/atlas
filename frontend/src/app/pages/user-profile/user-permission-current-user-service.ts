import { FormGroup } from '@angular/forms';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../core/components/permissions/form/application-permission-form-group';
import { inject } from '@angular/core';
import { UserService } from '../../core/auth/user/user.service';
import { ApplicationType, Permission } from '../../api';
import { UserPermissionProviderService } from '../../core/components/permissions/application-permission/user-permission-provider-service';

export class UserPermissionCurrentUserService
  implements UserPermissionProviderService
{
  userService = inject(UserService);

  showAllSpecialPermissions(): boolean {
    return false;
  }

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permission: Permission =
      this.userService.currentUser!.permissions.find(
        (i) => i.application === application
      )!;
    const applicationPermissionFormGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        application,
        permission
      );
    applicationPermissionFormGroup.disable();
    return applicationPermissionFormGroup;
  }
}
