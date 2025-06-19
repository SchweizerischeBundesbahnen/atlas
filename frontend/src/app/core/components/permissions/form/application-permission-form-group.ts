import { FormControl, FormGroup } from '@angular/forms';
import { ApplicationRole, ApplicationType } from 'src/app/api';

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
