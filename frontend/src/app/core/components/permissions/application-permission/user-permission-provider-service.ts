import { ApplicationType } from '../../../../api';
import { FormGroup } from '@angular/forms';
import { ApplicationPermission } from '../form/application-permission-form-group';

export abstract class UserPermissionProviderService {
  abstract loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission>;

  abstract showAllSpecialPermissions(): boolean;
}
