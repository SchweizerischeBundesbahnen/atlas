import { FormGroup } from '@angular/forms';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { inject, Injectable } from '@angular/core';
import {
  ApplicationType,
  ClientCredential,
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
export class UserPermissionGivenClientService
  implements UserPermissionProviderService
{
  clientCredential!: ClientCredential;
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;

  showAllSpecialPermissions(): boolean {
    return true;
  }

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permissions: Permission = Array.from(
      this.clientCredential.permissions!
    ).find((i) => i.application === application)!;

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
