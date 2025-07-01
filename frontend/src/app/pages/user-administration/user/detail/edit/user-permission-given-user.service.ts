import { FormGroup } from '@angular/forms';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { Injectable } from '@angular/core';
import { ApplicationType, Permission, User } from '../../../../../api';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../../../../core/components/permissions/form/application-permission-form-group';

@Injectable({
  providedIn: 'root',
})
export class UserPermissionGivenUserService extends UserPermissionProviderService {
  user!: User;
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;

  showAllSpecialPermissions(): boolean {
    return true;
  }

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permission: Permission = super.getPermission(
      this.user.permissions,
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
