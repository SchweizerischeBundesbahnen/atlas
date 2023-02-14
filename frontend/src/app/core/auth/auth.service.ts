import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { User } from '../components/user/user';
import { Pages } from '../../pages/pages';
import jwtDecode from 'jwt-decode';
import { Role } from './role';
import {
  ApplicationRole,
  ApplicationType,
  UserAdministrationService,
  UserPermissionVersionModel,
} from '../../api';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  readonly eventUserComponentNotification: EventEmitter<User> = new EventEmitter<User>();
  private readonly REQUESTED_ROUTE_STORAGE_KEY = 'requested_route';

  private permissions: UserPermissionVersionModel[] = [];

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private userAdministrationService: UserAdministrationService
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
        Object.values(Role).includes(role as Role)
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
    permissions: UserPermissionVersionModel[],
    isAdmin: boolean
  ): boolean {
    if (isAdmin) {
      return true;
    }
    const applicationPermission = AuthService.getApplicationPermission(
      permissions,
      applicationType
    );
    return AuthService.getRolesAllowedToCreate(applicationType).includes(
      applicationPermission.role!
    );
  }

  // Determines if we show the edit button
  static hasPermissionsToWriteWithPermissions(
    applicationType: ApplicationType,
    sboid: string | undefined,
    permissions: UserPermissionVersionModel[],
    isAdmin: boolean
  ): boolean {
    if (isAdmin) {
      return true;
    }
    const applicationPermission = AuthService.getApplicationPermission(
      permissions,
      applicationType
    );
    if (
      AuthService.getRolesAllowedToUpdate(applicationType).includes(applicationPermission.role!)
    ) {
      return true;
    }

    // Writer must be explicitely permitted to edit for a specific sboid
    if (sboid && ApplicationRole.Writer === applicationPermission.role!) {
      return Array.from(applicationPermission.sboids!.values()).includes(sboid);
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
    permissions: UserPermissionVersionModel[],
    applicationType: ApplicationType
  ): UserPermissionVersionModel {
    const applicationPermissions = permissions.filter(
      (permission) => permission.application === applicationType
    );
    if (applicationPermissions.length === 1) {
      return applicationPermissions[0];
    }
    return { application: applicationType, role: ApplicationRole.Reader, sboids: [] };
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
    });
  }

  getPermissions() {
    return this.permissions;
  }

  hasPermissionsToCreate(applicationType: ApplicationType): boolean {
    return AuthService.hasPermissionsToCreateWithPermissions(
      applicationType,
      this.permissions,
      this.isAdmin
    );
  }

  hasPermissionsToWrite(applicationType: ApplicationType, sboid: string | undefined): boolean {
    return AuthService.hasPermissionsToWriteWithPermissions(
      applicationType,
      sboid,
      this.permissions,
      this.isAdmin
    );
  }

  getApplicationUserPermission(applicationType: ApplicationType) {
    return AuthService.getApplicationPermission(this.getPermissions(), applicationType);
  }

  isAtLeastSupervisor(applicationType: ApplicationType) {
    const applicationUserPermission = this.getApplicationUserPermission(applicationType);
    return this.isAdmin || applicationUserPermission.role === ApplicationRole.Supervisor;
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
