import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { User } from '../components/user/user';
import { Pages } from '../../pages/pages';
import jwtDecode from 'jwt-decode';
import { Role } from './role';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  readonly eventUserComponentNotification: EventEmitter<User> = new EventEmitter<User>();
  private readonly REQUESTED_ROUTE_STORAGE_KEY = 'requested_route';

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

  constructor(private oauthService: OAuthService, private router: Router) {
    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();

    this.oauthService.loadDiscoveryDocumentAndLogin().then(() => {
      if (this.loggedIn) {
        this.eventUserComponentNotification.emit(this.claims);
        if (this.hasRole(Role.AtlasAdmin)) {
          Pages.enabledPages = [...Pages.pages, ...Pages.adminPages];
        }
        this.router.navigate([sessionStorage.getItem(this.REQUESTED_ROUTE_STORAGE_KEY)]).then();
      }
    });
  }

  login() {
    sessionStorage.setItem(this.REQUESTED_ROUTE_STORAGE_KEY, location.pathname);
    // App will be reloaded after initCodeFlow
    this.oauthService.initCodeFlow();
  }

  logout() {
    this.oauthService.logOut(true);
    Pages.enabledPages = Pages.pages;
    return this.router.navigate([Pages.HOME.path]);
  }

  get roles(): Role[] {
    if (this.accessToken) {
      return this.decodeAccessToken().roles.filter((role) =>
        Object.values(Role).includes(role as Role)
      ) as Role[];
    }
    return [];
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
