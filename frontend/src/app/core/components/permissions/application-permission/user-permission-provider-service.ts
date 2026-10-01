import { inject, Injector, runInInjectionContext, signal } from '@angular/core';
import { ApplicationRole, ApplicationType, Permission } from '../../../../api';
import { ApplicationPermissionForm, ApplicationPermissionFormBuilder } from '../form/application-permission-form-group';

export abstract class UserPermissionProviderService {
  private readonly injector = inject(Injector);
  readonly currentForm = signal<ApplicationPermissionForm | undefined>(undefined);
  readonly editable = signal(false);

  abstract loadForm(application: ApplicationType): void;
  abstract showAllSpecialPermissions(): boolean;

  setEditable(editable: boolean): void {
    this.editable.set(editable);
  }

  protected setPermissionForm(application: ApplicationType, permission: Permission): void {
    this.editable.set(false);
    this.currentForm.set(
      runInInjectionContext(this.injector, () =>
        ApplicationPermissionFormBuilder.buildAndFillForm(application, permission, this.editable)
      )
    );
  }

  protected getPermission(permissions: Iterable<Permission>, application: ApplicationType): Permission {
    return (
      Array.from(permissions).find((i) => i.application === application) ?? {
        role: ApplicationRole.Reader,
        application,
        permissionRestrictions: [],
      }
    );
  }
}
