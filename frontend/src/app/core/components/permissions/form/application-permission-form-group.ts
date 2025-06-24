import { FormControl, FormGroup } from '@angular/forms';
import {
  ApplicationRole,
  ApplicationType,
  Permission,
  PermissionRestrictionType,
} from 'src/app/api';

export interface ApplicationPermission {
  role: FormControl<ApplicationRole | null | undefined>;
  permissions: FormGroup<PermissionRestriction>;
}

export interface PermissionRestriction {
  sboidsRestrictions?: FormControl<string[] | null | undefined>;
  countryRestrictions?: FormControl<string[] | null | undefined>;
  cantonRestrictions?: FormControl<string[] | null | undefined>;
  bulkImportRestriction?: FormControl<boolean | null | undefined>;
  infoPlusTerminationVote?: FormControl<boolean | null | undefined>;
  novaTerminationVote?: FormControl<boolean | null | undefined>;
}

export class ApplicationPermissionFormGroupBuilder {
  static buildAndFillFormGroup(
    application: ApplicationType,
    permission: Permission
  ) {
    const formGroup: FormGroup<ApplicationPermission> =
      this.buildFormGroup(application);
    formGroup.controls.role.setValue(permission.role);

    formGroup.controls.permissions.controls.cantonRestrictions?.setValue(
      permission.permissionRestrictions
        .filter((i) => i.type === PermissionRestrictionType.Canton)
        .map((i) => i.valueAsString!)
    );
    formGroup.controls.permissions.controls.countryRestrictions?.setValue(
      permission.permissionRestrictions
        .filter((i) => i.type === PermissionRestrictionType.Country)
        .map((i) => i.valueAsString!)
    );
    formGroup.controls.permissions.controls.sboidsRestrictions?.setValue(
      permission.permissionRestrictions
        .filter(
          (i) => i.type === PermissionRestrictionType.BusinessOrganisation
        )
        .map((i) => i.valueAsString!)
    );
    formGroup.controls.permissions.controls.bulkImportRestriction?.setValue(
      permission.permissionRestrictions.some(
        (i) =>
          i.type === PermissionRestrictionType.BulkImport &&
          i.valueAsString == 'true'
      )
    );
    formGroup.controls.permissions.controls.infoPlusTerminationVote?.setValue(
      permission.permissionRestrictions.some(
        (i) =>
          i.type === PermissionRestrictionType.InfoPlusTerminationVote &&
          i.valueAsString == 'true'
      )
    );
    formGroup.controls.permissions.controls.novaTerminationVote?.setValue(
      permission.permissionRestrictions.some(
        (i) =>
          i.type === PermissionRestrictionType.NovaTerminationVote &&
          i.valueAsString == 'true'
      )
    );
    return formGroup;
  }

  static buildFormGroup(
    application: ApplicationType
  ): FormGroup<ApplicationPermission> {
    return this.APPLICATION_FORM_GROUPS[application];
  }

  private static APPLICATION_FORM_GROUPS: {
    [application in ApplicationType]: FormGroup<ApplicationPermission>;
  } = {
    TTFN: new FormGroup({
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({
        sboidsRestrictions: new FormControl([]),
        bulkImportRestriction: new FormControl(),
      }),
    }),
    LIDI: new FormGroup({
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({
        sboidsRestrictions: new FormControl([]),
        bulkImportRestriction: new FormControl(),
      }),
    }),
    BODI: new FormGroup({
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({}),
    }),
    TIMETABLE_HEARING: new FormGroup({
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({
        cantonRestrictions: new FormControl([]),
      }),
    }),
    SEPODI: new FormGroup({
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({
        countryRestrictions: new FormControl([]),
        sboidsRestrictions: new FormControl([]),
        bulkImportRestriction: new FormControl(),
        novaTerminationVote: new FormControl(),
        infoPlusTerminationVote: new FormControl(),
      }),
    }),
    PRM: new FormGroup({
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({
        sboidsRestrictions: new FormControl([]),
        bulkImportRestriction: new FormControl(),
      }),
    }),
  };
}
