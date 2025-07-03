import { FormGroup } from '@angular/forms';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../core/components/permissions/form/application-permission-form-group';
import { inject } from '@angular/core';
import { UserService } from '../../core/auth/user/user.service';
import { ApplicationType, Permission } from '../../api';
import { UserPermissionProviderService } from '../../core/components/permissions/application-permission/user-permission-provider-service';

export class UserPermissionCurrentUserService extends UserPermissionProviderService {
  userService = inject(UserService);
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;

  getCurrentForm(): FormGroup<ApplicationPermission> | undefined {
    return this.applicationPermissionFormGroup;
  }

  showAllSpecialPermissions(): boolean {
    return false;
  }

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permission: Permission = super.getPermission(
      this.userService.currentUser!.permissions,
      application
    );
    const applicationPermissionFormGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        application,
        permission
      );
    applicationPermissionFormGroup.disable();
    this.applicationPermissionFormGroup = applicationPermissionFormGroup;
    return applicationPermissionFormGroup;
  }
}
