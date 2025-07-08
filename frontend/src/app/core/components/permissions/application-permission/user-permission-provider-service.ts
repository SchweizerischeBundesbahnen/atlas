import { ApplicationRole, ApplicationType, Permission } from '../../../../api';
import { FormGroup } from '@angular/forms';
import { ApplicationPermission } from '../form/application-permission-form-group';
import { Subject } from 'rxjs';

export abstract class UserPermissionProviderService {
  formChanged = new Subject<FormGroup<ApplicationPermission>>();

  abstract loadFormGroup(application: ApplicationType): void;

  abstract showAllSpecialPermissions(): boolean;

  abstract getCurrentForm(): FormGroup<ApplicationPermission> | undefined;

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
