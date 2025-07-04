import { FormControl, FormGroup } from '@angular/forms';
import {
  ApplicationRole,
  ApplicationType,
  BulkImportPermissionRestrictionModel,
  CantonPermissionRestrictionModel,
  CountryPermissionRestrictionModel,
  Permission,
  PermissionRestrictionType,
  SboidPermissionRestrictionModel,
} from 'src/app/api';
import { InfoPlusTerminationVotePermissionRestrictionModel } from '../../../../api/model/infoPlusTerminationVotePermissionRestrictionModel';
import { NovaTerminationVotePermissionRestrictionModel } from '../../../../api/model/novaTerminationVotePermissionRestrictionModel';
import {
  ApplicationPermissionConfig,
  RoleConfig,
} from '../application-permission/application-permission.config';
import { PermissionPermissionRestrictionsInner } from '../../../../api/model/permissionPermissionRestrictionsInner';

export interface ApplicationPermission {
  application: FormControl<ApplicationType | null | undefined>;
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
    const formGroup: FormGroup<ApplicationPermission> = this.buildFormGroup();
    formGroup.controls.role.setValue(permission.role);
    formGroup.controls.application.setValue(application);

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

  static buildFormGroup(): FormGroup<ApplicationPermission> {
    return new FormGroup({
      application: new FormControl(),
      role: new FormControl(),
      permissions: new FormGroup<PermissionRestriction>({
        countryRestrictions: new FormControl([]),
        sboidsRestrictions: new FormControl([]),
        bulkImportRestriction: new FormControl(),
        novaTerminationVote: new FormControl(),
        infoPlusTerminationVote: new FormControl(),
      }),
    });
  }

  static formToModel(form: FormGroup<ApplicationPermission>): Permission {
    const application = form.controls.application.value!;
    const role = form.controls.role.value!;

    const roleConfig = ApplicationPermissionConfig.getByRole(application, role);

    const sboidRestrictions = this.getSboidRestrictions(form, roleConfig);
    const countryRestrictions = this.getCountryRestrictions(form, roleConfig);
    const cantonRestrictions = this.getCantonRestrictions(form, roleConfig);

    const bulkImportRestriction = this.getBulkImportRestriction(
      form,
      roleConfig
    );
    const infoPlusTerminationVoteRestriction =
      this.getInfoPlusTerminationRestriction(form, roleConfig);
    const novaTerminationVoteRestriction = this.getNovaTerminationRestriction(
      form,
      roleConfig
    );

    const permissionRestrictions: PermissionPermissionRestrictionsInner[] = [];
    permissionRestrictions.push(...sboidRestrictions);
    permissionRestrictions.push(...countryRestrictions);
    permissionRestrictions.push(...cantonRestrictions);
    if (bulkImportRestriction) {
      permissionRestrictions.push(bulkImportRestriction);
    }
    if (infoPlusTerminationVoteRestriction) {
      permissionRestrictions.push(infoPlusTerminationVoteRestriction);
    }
    if (novaTerminationVoteRestriction) {
      permissionRestrictions.push(novaTerminationVoteRestriction);
    }
    return {
      role: role,
      application: application,
      permissionRestrictions: permissionRestrictions,
    };
  }

  private static getSboidRestrictions(
    form: FormGroup<ApplicationPermission>,
    roleConfig: RoleConfig
  ) {
    if (
      roleConfig.permissions.restrictions.includes(
        PermissionRestrictionType.BusinessOrganisation
      )
    ) {
      const sboids =
        form.controls.permissions.controls.sboidsRestrictions?.value ?? [];
      const sboidRestrictions: SboidPermissionRestrictionModel[] = sboids.map(
        (sboid) => ({
          type: PermissionRestrictionType.BusinessOrganisation,
          valueAsString: sboid,
        })
      );
      return sboidRestrictions;
    }
    return [];
  }

  private static getCountryRestrictions(
    form: FormGroup<ApplicationPermission>,
    roleConfig: RoleConfig
  ) {
    if (
      roleConfig.permissions.restrictions.includes(
        PermissionRestrictionType.Country
      )
    ) {
      const countries =
        form.controls.permissions.controls.countryRestrictions?.value ?? [];
      const countryRestrictions: CountryPermissionRestrictionModel[] = countries
        .filter((country) => !!country)
        .map((country) => ({
          type: PermissionRestrictionType.Country,
          valueAsString: country,
        }));
      return countryRestrictions;
    }
    return [];
  }

  private static getCantonRestrictions(
    form: FormGroup<ApplicationPermission>,
    roleConfig: RoleConfig
  ) {
    if (
      roleConfig.permissions.restrictions.includes(
        PermissionRestrictionType.Canton
      )
    ) {
      const cantons =
        form.controls.permissions.controls.cantonRestrictions?.value ?? [];
      const cantonRestrictions: CantonPermissionRestrictionModel[] =
        cantons.map((canton) => ({
          type: PermissionRestrictionType.Canton,
          valueAsString: canton,
        }));
      return cantonRestrictions;
    }
    return [];
  }

  private static getBulkImportRestriction(
    form: FormGroup<ApplicationPermission>,
    roleConfig: RoleConfig
  ) {
    if (
      roleConfig.permissions.specialPermissions.includes(
        PermissionRestrictionType.BulkImport
      )
    ) {
      const bulkImport =
        form.controls.permissions.controls.bulkImportRestriction?.value ??
        false;
      const bulkImportRestriction: BulkImportPermissionRestrictionModel = {
        type: PermissionRestrictionType.BulkImport,
        valueAsString: bulkImport.toString(),
      };
      return bulkImportRestriction;
    }
    return undefined;
  }

  private static getNovaTerminationRestriction(
    form: FormGroup<ApplicationPermission>,
    roleConfig: RoleConfig
  ) {
    if (
      roleConfig.permissions.specialPermissions.includes(
        PermissionRestrictionType.NovaTerminationVote
      )
    ) {
      const novaTerminationVote =
        form.controls.permissions.controls.novaTerminationVote?.value ?? false;
      const novaTerminationVoteRestriction: NovaTerminationVotePermissionRestrictionModel =
        {
          type: PermissionRestrictionType.NovaTerminationVote,
          valueAsString: novaTerminationVote.toString(),
        };
      return novaTerminationVoteRestriction;
    }
    return undefined;
  }

  private static getInfoPlusTerminationRestriction(
    form: FormGroup<ApplicationPermission>,
    roleConfig: RoleConfig
  ) {
    if (
      roleConfig.permissions.specialPermissions.includes(
        PermissionRestrictionType.InfoPlusTerminationVote
      )
    ) {
      const infoPlusTerminationVote =
        form.controls.permissions.controls.infoPlusTerminationVote?.value ??
        false;
      const infoPlusTerminationVoteRestriction: InfoPlusTerminationVotePermissionRestrictionModel =
        {
          type: PermissionRestrictionType.InfoPlusTerminationVote,
          valueAsString: infoPlusTerminationVote.toString(),
        };
      return infoPlusTerminationVoteRestriction;
    }
    return undefined;
  }
}
