import {Injectable} from '@angular/core';
import {UserService} from "../user/user.service";
import {ApplicationRole, ApplicationType, Permission} from "../../../api";
import {Cantons} from "../../cantons/Cantons";

@Injectable({
  providedIn: 'root',
})
export class PermissionService {

  constructor(private userService: UserService) {
  }

  public static getSboidRestrictions(userPermission: Permission): string[] {
    return userPermission.permissionRestrictions.map((restriction) => restriction.valueAsString!);
  }

  hasPermissionsToCreate(applicationType: ApplicationType): boolean {
    return PermissionService.hasPermissionsToCreateWithPermissions(
      applicationType,
      this.permissions,
      this.isAdmin,
    );
  }

  hasPermissionsToWrite(applicationType: ApplicationType, sboid: string | undefined): boolean {
    return PermissionService.hasPermissionsToWriteWithPermissions(
      applicationType,
      sboid,
      this.permissions,
      this.isAdmin,
    );
  }

  hasWritePermissionsToForCanton(
    applicationType: ApplicationType,
    canton: string | undefined,
  ): boolean {
    return PermissionService.hasPermissionToWriteOnCanton(
      applicationType,
      canton,
      this.permissions,
      this.isAdmin,
    );
  }

  getApplicationUserPermission(applicationType: ApplicationType) {
    return PermissionService.getApplicationPermission(this.permissions, applicationType);
  }

  isAtLeastSupervisor(applicationType: ApplicationType) {
    const applicationUserPermission = this.getApplicationUserPermission(applicationType);
    return this.isAdmin || applicationUserPermission.role === ApplicationRole.Supervisor;
  }

  mayAccessTimetableHearing() {
    const applicationUserPermission = this.getApplicationUserPermission(
      ApplicationType.TimetableHearing,
    );
    return (
      this.isAdmin ||
      [ApplicationRole.Supervisor, ApplicationRole.Writer, ApplicationRole.ExplicitReader].includes(
        applicationUserPermission.role,
      )
    );
  }

  mayAccessTtfn() {
    const applicationUserPermission = this.getApplicationUserPermission(ApplicationType.Ttfn);
    return (
      this.isAdmin ||
      [ApplicationRole.Supervisor].includes(applicationUserPermission.role)
    );
  }

  mayAccessMassImport() {
    const applicationUserPermission = this.getApplicationUserPermission(ApplicationType.MassImport);
    return (
      this.isAdmin ||
      [ApplicationRole.Supervisor].includes(applicationUserPermission.role)
    );
  }

  get permissions() {
    return this.userService.permissions;
  }

  get isAdmin() {
    return this.userService.isAdmin;
  }

  // Determines if we show the create button
  static hasPermissionsToCreateWithPermissions(
    applicationType: ApplicationType,
    permissions: Permission[],
    isAdmin: boolean,
  ): boolean {
    if (isAdmin) {
      return true;
    }
    const applicationPermission = PermissionService.getApplicationPermission(
      permissions,
      applicationType,
    );
    return PermissionService.getRolesAllowedToCreate(applicationType).includes(applicationPermission.role);
  }

  static hasPermissionToWriteOnCanton(
    applicationType: ApplicationType,
    canton: string | undefined,
    permissions: Permission[],
    isAdmin: boolean,
  ): boolean {
    if (!canton || !applicationType) {
      throw new Error('Canton button needs canton and applicationtype');
    }

    const applicationUserPermission = PermissionService.getApplicationPermission(
      permissions,
      applicationType,
    );
    if (isAdmin || applicationUserPermission.role === ApplicationRole.Supervisor) {
      return true;
    }
    if (applicationUserPermission.role === ApplicationRole.Writer) {
      const allowedSwissCantons = applicationUserPermission.permissionRestrictions.map(
        (restriction) => restriction.valueAsString,
      );
      return allowedSwissCantons.includes(Cantons.getSwissCantonEnum(canton));
    }
    return false;
  }

  // Determines if we show the edit button
  static hasPermissionsToWriteWithPermissions(
    applicationType: ApplicationType,
    sboid: string | undefined,
    permissions: Permission[],
    isAdmin: boolean,
  ): boolean {
    if (isAdmin) {
      return true;
    }
    const applicationPermission = PermissionService.getApplicationPermission(
      permissions,
      applicationType,
    );
    if (PermissionService.getRolesAllowedToUpdate(applicationType).includes(applicationPermission.role)) {
      return true;
    }

    // Writer must be explicitely permitted to edit for a specific sboid
    if (sboid && ApplicationRole.Writer === applicationPermission.role) {
      return PermissionService.getSboidRestrictions(applicationPermission).includes(sboid);
    }
    return false;
  }

  private static getRolesAllowedToCreate(applicationType: ApplicationType) {
    let rolesAllowedToCreate = [
      ApplicationRole.Supervisor,
      ApplicationRole.SuperUser,
      ApplicationRole.Writer,
    ];
    // Supervisor is allowed to create BusinessOrganisation
    if (ApplicationType.Bodi === applicationType) {
      rolesAllowedToCreate = [ApplicationRole.Supervisor];
    }
    return rolesAllowedToCreate;
  }

  private static getRolesAllowedToUpdate(applicationType: ApplicationType) {
    let rolesAllowedToUpdate = [ApplicationRole.Supervisor, ApplicationRole.SuperUser];
    // Supervisor is allowed to update BusinessOrganisation
    if (ApplicationType.Bodi === applicationType) {
      rolesAllowedToUpdate = [ApplicationRole.Supervisor];
    }
    return rolesAllowedToUpdate;
  }

  private static getApplicationPermission(
    permissions: Permission[],
    applicationType: ApplicationType,
  ): Permission {
    const applicationPermissions = permissions.filter(
      (permission) => permission.application === applicationType,
    );
    if (applicationPermissions.length === 1) {
      return applicationPermissions[0];
    }
    return {
      application: applicationType,
      role: ApplicationRole.Reader,
      permissionRestrictions: [],
    };
  }
}
