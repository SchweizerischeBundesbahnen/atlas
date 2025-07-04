import {
  ApplicationRole,
  ApplicationType,
  Country,
  Permission,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../api';
import { ApplicationPermissionFormGroupBuilder } from './application-permission-form-group';

describe('ApplicationPermissionFormGroupBuilder', () => {
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

    const formGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        ApplicationType.Sepodi,
        existingPermission
      );

    const permission =
      ApplicationPermissionFormGroupBuilder.formToModel(formGroup);
    expect(permission.role).toEqual(existingPermission.role);
    expect(permission.permissionRestrictions).toEqual(
      jasmine.arrayWithExactContents(existingPermission.permissionRestrictions)
    );
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

    const formGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        ApplicationType.Sepodi,
        existingPermission
      );

    const permission =
      ApplicationPermissionFormGroupBuilder.formToModel(formGroup);
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

    const formGroup =
      ApplicationPermissionFormGroupBuilder.buildAndFillFormGroup(
        ApplicationType.TimetableHearing,
        existingPermission
      );

    const permission =
      ApplicationPermissionFormGroupBuilder.formToModel(formGroup);
    expect(permission).toEqual(existingPermission);
  });
});
