import { FormGroup } from '@angular/forms';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../core/components/permissions/form/application-permission-form-group';
import { inject } from '@angular/core';
import { UserService } from '../../core/auth/user/user.service';
import { ApplicationType, Permission } from '../../api';
import { UserPermissionProviderService } from './user-permission-provider-service';

export class UserProfileService implements UserPermissionProviderService {
  userService = inject(UserService);

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permissions: Permission =
      this.userService.currentUser!.permissions.find(
        (i) => i.application === application
      )!;
    const applicationPermissionFormGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        application,
        permissions
      );
    // applicationPermissionFormGroup.disable();
    return applicationPermissionFormGroup;
  }
}
