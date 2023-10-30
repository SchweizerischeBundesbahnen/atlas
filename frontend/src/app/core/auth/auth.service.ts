import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { User } from '../components/user/user';
import { Pages } from '../../pages/pages';
import { jwtDecode } from 'jwt-decode';
import { Role } from './role';
import { ApplicationRole, ApplicationType, Permission, UserAdministrationService } from '../../api';
import { BehaviorSubject } from 'rxjs';
import { Cantons } from '../cantons/Cantons';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  readonly eventUserComponentNotification: EventEmitter<User> = new EventEmitter<User>();
  permissionsLoaded = new BehaviorSubject(false);
  private readonly REQUESTED_ROUTE_STORAGE_KEY = 'requested_route';
  private permissions: Permission[] = [];

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private userAdministrationService: UserAdministrationService,
  ) {
    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();

    this.oauthService.loadDiscoveryDocumentAndLogin().then(() => {
      if (this.loggedIn) {
        this.eventUserComponentNotification.emit(this.claims);
        if (this.accessToken) {
          this.loadPermissions();
        }
        if (this.hasRole(Role.AtlasAdmin)) {
          Pages.viewablePages = [...Pages.pages, ...Pages.adminPages];
        }
        this.router
          .navigateByUrl(sessionStorage.getItem(this.REQUESTED_ROUTE_STORAGE_KEY) ?? '')
          .then();
      }
    });
  }

  get claims() {
    return this.oauthService.getIdentityClaims() as User;
  }

  get accessToken() {
    return this.oauthService.getAccessToken();
  }

  get scopes() {
    return this.oauthService.getGrantedScopes() as string[];
  }

  get loggedIn() {
    return !!this.claims;
  }

  get roles(): Role[] {
    if (this.accessToken) {
      return this.decodeAccessToken().roles.filter((role) =>
        Object.values(Role).includes(role as Role),
      ) as Role[];
    }
    return [];
  }

  get isAdmin(): boolean {
    return this.hasRole(Role.AtlasAdmin);
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
    const applicationPermission = AuthService.getApplicationPermission(
      permissions,
      applicationType,
    );
    return AuthService.getRolesAllowedToCreate(applicationType).includes(
      applicationPermission.role!,
    );
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

    const applicationUserPermission = AuthService.getApplicationPermission(
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
    const applicationPermission = AuthService.getApplicationPermission(
      permissions,
      applicationType,
    );
    if (
      AuthService.getRolesAllowedToUpdate(applicationType).includes(applicationPermission.role!)
    ) {
      return true;
    }

    // Writer must be explicitely permitted to edit for a specific sboid
    if (sboid && ApplicationRole.Writer === applicationPermission.role!) {
      return AuthService.getSboidRestrictions(applicationPermission).includes(sboid);
    }
    return false;
  }

  public static getSboidRestrictions(userPermission: Permission): string[] {
    return userPermission.permissionRestrictions!.map((restriction) => restriction.valueAsString!);
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

  login() {
    sessionStorage.setItem(this.REQUESTED_ROUTE_STORAGE_KEY, location.pathname + location.search);
    // App will be reloaded after initCodeFlow
    this.oauthService.initCodeFlow();
  }

  logout() {
    this.oauthService.logOut(true);
    Pages.viewablePages = Pages.pages;
    return this.router.navigate([Pages.HOME.path]);
  }

  loadPermissions() {
    this.userAdministrationService.getCurrentUser().subscribe((response) => {
      this.permissions = response.permissions ? Array.from(response.permissions) : [];
      this.eventUserComponentNotification.emit(this.claims);

      if (this.mayAccessTimetableHearing()) {
        Pages.viewablePages.push(Pages.TTH);
      }
      this.permissionsLoaded.next(true);
    });
  }

  getPermissions() {
    return this.permissions;
  }

  hasPermissionsToCreate(applicationType: ApplicationType): boolean {
    return AuthService.hasPermissionsToCreateWithPermissions(
      applicationType,
      this.permissions,
      this.isAdmin,
    );
  }

  hasPermissionsToWrite(applicationType: ApplicationType, sboid: string | undefined): boolean {
    return AuthService.hasPermissionsToWriteWithPermissions(
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
    return AuthService.hasPermissionToWriteOnCanton(
      applicationType,
      canton,
      this.permissions,
      this.isAdmin,
    );
  }

  getApplicationUserPermission(applicationType: ApplicationType) {
    return AuthService.getApplicationPermission(this.getPermissions(), applicationType);
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

  hasRole(role: Role): boolean {
    return this.hasAnyRole([role]);
  }

  hasAnyRole(roles: Role[]): boolean {
    return this.containsAnyRole(roles, this.roles);
  }

  containsAnyRole(roles: Role[], userRoles: Role[]): boolean {
    return userRoles.some((r) => roles.includes(r));
  }

  private decodeAccessToken(): User {
    return jwtDecode(this.accessToken);
  }
}
