import { FormGroup } from '@angular/forms';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { Injectable } from '@angular/core';
import {
  ApplicationType,
  ClientCredential,
  Permission,
} from '../../../../../api';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../../../../core/components/permissions/form/application-permission-form-group';

@Injectable({
  providedIn: 'root',
})
export class UserPermissionGivenClientService extends UserPermissionProviderService {
  clientCredential!: ClientCredential;
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;

  showAllSpecialPermissions(): boolean {
    return true;
  }

  loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    const permissions: Permission = super.getPermission(
      this.clientCredential.permissions!,
      application
    );

    const applicationPermissionFormGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        application,
        permissions
      );
    applicationPermissionFormGroup.disable();
    this.applicationPermissionFormGroup = applicationPermissionFormGroup;
    return applicationPermissionFormGroup;
  }

  getCurrentForm(): FormGroup<ApplicationPermission> | undefined {
    return this.applicationPermissionFormGroup;
  }
}
