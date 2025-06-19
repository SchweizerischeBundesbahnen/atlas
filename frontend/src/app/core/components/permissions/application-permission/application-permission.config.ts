import {
  ApplicationRole,
  ApplicationType,
  PermissionRestrictionType,
} from '../../../../api';

export interface ApplicationConfig {
  roles: RoleConfig[];
}

export interface RoleConfig {
  role: ApplicationRole;
  permissions: PermissionsConfig;
}

export interface PermissionsConfig {
  restrictions: PermissionRestrictionType[];
  specialPermissions: PermissionRestrictionType[];
}

export class ApplicationPermissionConfig {
  public static get(application: ApplicationType): ApplicationConfig {
    return this.CONFIG[application];
  }

  public static getRoles(application: ApplicationType): ApplicationRole[] {
    return this.CONFIG[application].roles.map((i) => i.role);
  }

  private static readonly CONFIG: {
    [application in ApplicationType]: ApplicationConfig;
  } = {
    TTFN: {
      roles: [
        {
          role: ApplicationRole.Reader,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.Writer,
          permissions: {
            restrictions: [PermissionRestrictionType.BusinessOrganisation],
            specialPermissions: [PermissionRestrictionType.BulkImport],
          },
        },
        {
          role: ApplicationRole.SuperUser,
          permissions: {
            restrictions: [],
            specialPermissions: [PermissionRestrictionType.BulkImport],
          },
        },
        {
          role: ApplicationRole.Supervisor,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
      ],
    },
    LIDI: {
      roles: [
        {
          role: ApplicationRole.Reader,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.Writer,
          permissions: {
            restrictions: [PermissionRestrictionType.BusinessOrganisation],
            specialPermissions: [PermissionRestrictionType.BulkImport],
          },
        },
        {
          role: ApplicationRole.SuperUser,
          permissions: {
            restrictions: [],
            specialPermissions: [PermissionRestrictionType.BulkImport],
          },
        },
        {
          role: ApplicationRole.Supervisor,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
      ],
    },
    BODI: {
      roles: [
        {
          role: ApplicationRole.Reader,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.Supervisor,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
      ],
    },
    TIMETABLE_HEARING: {
      roles: [
        {
          role: ApplicationRole.Reader,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.ExplicitReader,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.Writer,
          permissions: {
            restrictions: [PermissionRestrictionType.Canton],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.Supervisor,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
      ],
    },
    SEPODI: {
      roles: [
        {
          role: ApplicationRole.Reader,
          permissions: {
            restrictions: [],
            specialPermissions: [
              PermissionRestrictionType.NovaTerminationVote,
              PermissionRestrictionType.InfoPlusTerminationVote,
            ],
          },
        },
        {
          role: ApplicationRole.Writer,
          permissions: {
            restrictions: [
              PermissionRestrictionType.Country,
              PermissionRestrictionType.BusinessOrganisation,
            ],
            specialPermissions: [
              PermissionRestrictionType.NovaTerminationVote,
              PermissionRestrictionType.InfoPlusTerminationVote,
            ],
          },
        },
        {
          role: ApplicationRole.SuperUser,
          permissions: {
            restrictions: [PermissionRestrictionType.Country],
            specialPermissions: [
              PermissionRestrictionType.NovaTerminationVote,
              PermissionRestrictionType.InfoPlusTerminationVote,
            ],
          },
        },
        {
          role: ApplicationRole.Supervisor,
          permissions: {
            restrictions: [],
            specialPermissions: [
              PermissionRestrictionType.NovaTerminationVote,
              PermissionRestrictionType.InfoPlusTerminationVote,
            ],
          },
        },
      ],
    },
    PRM: {
      roles: [
        {
          role: ApplicationRole.Reader,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
        {
          role: ApplicationRole.Writer,
          permissions: {
            restrictions: [PermissionRestrictionType.BusinessOrganisation],
            specialPermissions: [PermissionRestrictionType.BulkImport],
          },
        },
        {
          role: ApplicationRole.SuperUser,
          permissions: {
            restrictions: [],
            specialPermissions: [PermissionRestrictionType.BulkImport],
          },
        },
        {
          role: ApplicationRole.Supervisor,
          permissions: {
            restrictions: [],
            specialPermissions: [],
          },
        },
      ],
    },
  };
}
