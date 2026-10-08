import { Signal, signal } from '@angular/core';
import { disabled, FieldTree, form } from '@angular/forms/signals';
import {
  ApplicationRole,
  ApplicationType,
  Country,
  Permission,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../api';
import { PermissionPermissionRestrictionsInner } from '../../../../api/model/permissionPermissionRestrictionsInner';
import { ApplicationPermissionConfig } from '../application-permission/application-permission.config';

export interface ApplicationPermission {
  application: ApplicationType;
  role: ApplicationRole;
  permissions: {
    sboidsRestrictions: string[];
    countryRestrictions: Country[];
    cantonRestrictions: SwissCanton[];
    bulkImportRestriction: boolean;
    infoPlusTerminationVote: boolean;
    novaTerminationVote: boolean;
    transportCompanyDossierAnswer: boolean;
  };
}

export type ApplicationPermissionForm = FieldTree<ApplicationPermission>;

type ApplicationPermissionInput = Partial<Omit<ApplicationPermission, 'permissions'>> & {
  permissions?: Partial<ApplicationPermission['permissions']>;
};

export class ApplicationPermissionFormBuilder {
  static buildAndFillForm(
    application: ApplicationType,
    permission: Permission,
    editable: Signal<boolean> = signal(true)
  ): ApplicationPermissionForm {
    const restrictions = permission.permissionRestrictions;
    const valuesOf = (type: PermissionRestrictionType) =>
      restrictions
        .filter((restriction) => restriction.type === type)
        .map((restriction) => restriction.valueAsString)
        .filter((restrictionValue) => restrictionValue !== undefined);
    const enabled = (type: PermissionRestrictionType) =>
      restrictions.some((restriction) => restriction.type === type && restriction.valueAsString === 'true');

    return this.buildForm(
      {
        application,
        role: permission.role,
        permissions: {
          sboidsRestrictions: valuesOf(PermissionRestrictionType.BusinessOrganisation),
          countryRestrictions: valuesOf(PermissionRestrictionType.Country) as Country[],
          cantonRestrictions: valuesOf(PermissionRestrictionType.Canton) as SwissCanton[],
          bulkImportRestriction: enabled(PermissionRestrictionType.BulkImport),
          infoPlusTerminationVote: enabled(PermissionRestrictionType.InfoPlusTerminationVote),
          novaTerminationVote: enabled(PermissionRestrictionType.NovaTerminationVote),
          transportCompanyDossierAnswer: enabled(PermissionRestrictionType.TransportCompanyDossierAnswer),
        },
      },
      editable
    );
  }

  static buildForm(
    {
      application = ApplicationType.Ttfn,
      role = ApplicationRole.Reader,
      permissions: {
        sboidsRestrictions = [],
        countryRestrictions = [],
        cantonRestrictions = [],
        bulkImportRestriction = false,
        infoPlusTerminationVote = false,
        novaTerminationVote = false,
        transportCompanyDossierAnswer = false,
      } = {},
    }: ApplicationPermissionInput = {},
    editable: Signal<boolean> = signal(true)
  ): ApplicationPermissionForm {
    const model: ApplicationPermission = {
      application,
      role,
      permissions: {
        sboidsRestrictions,
        countryRestrictions,
        cantonRestrictions,
        bulkImportRestriction,
        infoPlusTerminationVote,
        novaTerminationVote,
        transportCompanyDossierAnswer,
      },
    };
    return form(signal(model), (path) => disabled(path, { when: () => !editable() }));
  }

  static formToModel(permissionForm: ApplicationPermissionForm): Permission {
    const { application, role, permissions } = permissionForm().value();
    const roleConfig = ApplicationPermissionConfig.getByRole(application, role);
    if (!roleConfig) {
      throw new Error(`Unsupported role ${role} for application ${application}`);
    }
    const permissionRestrictions: PermissionPermissionRestrictionsInner[] = [];
    const addValues = (type: PermissionRestrictionType, values: string[]) => {
      if (roleConfig.permissions.restrictions.includes(type)) {
        permissionRestrictions.push(...values.filter(Boolean).map((valueAsString) => ({ type, valueAsString })));
      }
    };
    const addFlag = (type: PermissionRestrictionType, value: boolean) => {
      if (roleConfig.permissions.specialPermissions.includes(type)) {
        permissionRestrictions.push({ type, valueAsString: String(value) });
      }
    };

    addValues(PermissionRestrictionType.BusinessOrganisation, permissions.sboidsRestrictions);
    addValues(PermissionRestrictionType.Country, permissions.countryRestrictions);
    addValues(PermissionRestrictionType.Canton, permissions.cantonRestrictions);
    addFlag(PermissionRestrictionType.BulkImport, permissions.bulkImportRestriction);
    addFlag(PermissionRestrictionType.InfoPlusTerminationVote, permissions.infoPlusTerminationVote);
    addFlag(PermissionRestrictionType.NovaTerminationVote, permissions.novaTerminationVote);
    addFlag(PermissionRestrictionType.TransportCompanyDossierAnswer, permissions.transportCompanyDossierAnswer);

    return { role, application, permissionRestrictions };
  }
}
