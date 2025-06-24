import { ApplicationType } from '../../api';
import { FormGroup } from '@angular/forms';
import { ApplicationPermission } from '../../core/components/permissions/form/application-permission-form-group';

export abstract class UserPermissionProviderService {
  abstract loadFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission>;
}
