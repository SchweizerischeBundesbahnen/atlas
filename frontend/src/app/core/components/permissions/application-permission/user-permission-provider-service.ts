import { ApplicationRole, ApplicationType, Permission } from '../../../../api';
import { FormGroup } from '@angular/forms';
import { ApplicationPermission } from '../form/application-permission-form-group';

export abstract class UserPermissionProviderService {
  abstract loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission>;

  abstract showAllSpecialPermissions(): boolean;

  protected getPermission(
    permissions: Iterable<Permission>,
    application: ApplicationType
  ): Permission {
    return (
      Array.from(permissions).find((i) => i.application === application) ?? {
        role: ApplicationRole.Reader,
        application: application,
        permissionRestrictions: [],
      }
    );
  }
}
