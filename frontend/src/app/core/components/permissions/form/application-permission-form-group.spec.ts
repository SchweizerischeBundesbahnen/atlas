import { describe, expect, it } from 'vitest';
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import {
  ApplicationRole,
  ApplicationType,
  Country,
  Permission,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../api';
import { ApplicationPermissionFormBuilder } from './application-permission-form-group';

describe('ApplicationPermissionFormBuilder', () => {
  const buildForm: typeof ApplicationPermissionFormBuilder.buildAndFillForm = (...args) =>
    TestBed.runInInjectionContext(() => ApplicationPermissionFormBuilder.buildAndFillForm(...args));

  it('uses defaults when no model is supplied', () => {
    const permissionForm = TestBed.runInInjectionContext(() => ApplicationPermissionFormBuilder.buildForm());

    expect(permissionForm().value()).toEqual({
      application: ApplicationType.Ttfn,
      role: ApplicationRole.Reader,
      permissions: {
        sboidsRestrictions: [],
        countryRestrictions: [],
        cantonRestrictions: [],
        bulkImportRestriction: false,
        infoPlusTerminationVote: false,
        novaTerminationVote: false,
        transportCompanyDossierAnswer: false,
      },
    });
  });

  it('defaults omitted properties of a partial model and nested permissions', () => {
    const permissionForm = TestBed.runInInjectionContext(() =>
      ApplicationPermissionFormBuilder.buildForm({
        role: ApplicationRole.Writer,
        permissions: {
          countryRestrictions: [Country.Switzerland],
          bulkImportRestriction: true,
        },
      })
    );

    expect(permissionForm().value()).toEqual({
      application: ApplicationType.Ttfn,
      role: ApplicationRole.Writer,
      permissions: {
        sboidsRestrictions: [],
        countryRestrictions: [Country.Switzerland],
        cantonRestrictions: [],
        bulkImportRestriction: true,
        infoPlusTerminationVote: false,
        novaTerminationVote: false,
        transportCompanyDossierAnswer: false,
      },
    });
  });

  it('should build form for sepodi nova voter', () => {
    const existingPermission: Permission = {
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.Country,
          valueAsString: Country.Switzerland,
        },
        {
          type: PermissionRestrictionType.BusinessOrganisation,
          valueAsString: 'ch:1:sboid:10000',
        },
        {
          type: PermissionRestrictionType.BulkImport,
          valueAsString: 'true',
        },
        {
          type: PermissionRestrictionType.NovaTerminationVote,
          valueAsString: 'true',
        },
        {
          type: PermissionRestrictionType.InfoPlusTerminationVote,
          valueAsString: 'false',
        },
      ],
    };

    const permissionForm = buildForm(ApplicationType.Sepodi, existingPermission);

    const permission = ApplicationPermissionFormBuilder.formToModel(permissionForm);
    expect(permission.role).toEqual(existingPermission.role);
    expect(permission.permissionRestrictions).toEqual(
      expect.arrayContaining(existingPermission.permissionRestrictions)
    );
    expect(permission.permissionRestrictions.length).toBe(existingPermission.permissionRestrictions.length);
  });

  it('should build form for sepodi info plus voter', () => {
    const existingPermission: Permission = {
      role: ApplicationRole.Reader,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.InfoPlusTerminationVote,
          valueAsString: 'true',
        },
        {
          type: PermissionRestrictionType.NovaTerminationVote,
          valueAsString: 'false',
        },
      ],
    };

    const permissionForm = buildForm(ApplicationType.Sepodi, existingPermission);

    const permission = ApplicationPermissionFormBuilder.formToModel(permissionForm);
    expect(permission).toEqual(existingPermission);
  });

  it('should build form for tth bo answerer', () => {
    const existingPermission: Permission = {
      role: ApplicationRole.Reader,
      application: ApplicationType.TimetableHearing,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.TransportCompanyDossierAnswer,
          valueAsString: 'true',
        },
      ],
    };

    const permissionForm = buildForm(ApplicationType.TimetableHearing, existingPermission);

    const permission = ApplicationPermissionFormBuilder.formToModel(permissionForm);
    expect(permission).toEqual(existingPermission);
  });

  it('should build form for tth', () => {
    const existingPermission: Permission = {
      role: ApplicationRole.Writer,
      application: ApplicationType.TimetableHearing,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.Canton,
          valueAsString: SwissCanton.Aargau,
        },
      ],
    };

    const permissionForm = buildForm(ApplicationType.TimetableHearing, existingPermission);

    const permission = ApplicationPermissionFormBuilder.formToModel(permissionForm);
    expect(permission).toEqual(existingPermission);
  });

  it('preserves values when disabled and strips restrictions that are not allowed by the selected role', () => {
    const editable = signal(true);
    const permissionForm = buildForm(
      ApplicationType.Sepodi,
      {
        application: ApplicationType.Sepodi,
        role: ApplicationRole.Writer,
        permissionRestrictions: [
          { type: PermissionRestrictionType.BusinessOrganisation, valueAsString: 'ch:1:sboid:10000' },
          { type: PermissionRestrictionType.BulkImport, valueAsString: 'true' },
        ],
      },
      editable
    );
    permissionForm.role().value.set(ApplicationRole.Reader);
    editable.set(false);

    expect(permissionForm().disabled()).toBe(true);
    expect(permissionForm().value().permissions.sboidsRestrictions).toEqual(['ch:1:sboid:10000']);
    expect(ApplicationPermissionFormBuilder.formToModel(permissionForm).permissionRestrictions).toEqual([
      { type: PermissionRestrictionType.InfoPlusTerminationVote, valueAsString: 'false' },
      { type: PermissionRestrictionType.NovaTerminationVote, valueAsString: 'false' },
    ]);
  });

  it('throws rather than silently serializing an unsupported role', () => {
    const permissionForm = buildForm(ApplicationType.Bodi, {
      application: ApplicationType.Bodi,
      role: ApplicationRole.Reader,
      permissionRestrictions: [],
    });
    permissionForm.role().value.set(ApplicationRole.Writer);
    expect(() => ApplicationPermissionFormBuilder.formToModel(permissionForm)).toThrow('Unsupported role');
  });
});
