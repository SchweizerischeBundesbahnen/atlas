import { FormGroup } from '@angular/forms';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { inject, Injectable } from '@angular/core';
import {
  ApplicationType,
  Permission,
  User,
  UserAdministrationService,
} from '../../../../../api';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../../../../core/components/permissions/form/application-permission-form-group';

@Injectable({
  providedIn: 'root',
})
export class UserPermissionGivenUserService
  implements UserPermissionProviderService
{
  user!: User;
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;
  userAdministrationService = inject(UserAdministrationService);

  showAllSpecialPermissions(): boolean {
    return true;
  }

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permissions: Permission = Array.from(this.user.permissions!).find(
      (i) => i.application === application
    )!;

    const applicationPermissionFormGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        application,
        permissions
      );
    applicationPermissionFormGroup.disable();
    this.applicationPermissionFormGroup = applicationPermissionFormGroup;
    return applicationPermissionFormGroup;
  }
}
