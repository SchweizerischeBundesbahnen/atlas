import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { first } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { User } from '../components/user/user';
import { Pages } from '../../pages/pages';
import jwtDecode from 'jwt-decode';
import { Role, Roles } from './role';

const DEEP_LINK_URL_KEY = 'deepLinkUrl';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  eventUserComponentNotification: EventEmitter<User> = new EventEmitter<User>();

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
    if (
      window.location.href !== environment.authConfig.redirectUri &&
      sessionStorage.getItem(DEEP_LINK_URL_KEY) == null
    ) {
      sessionStorage.setItem(DEEP_LINK_URL_KEY, location.pathname);
    }

    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();
    this.oauthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (!this.oauthService.hasValidIdToken() || !this.oauthService.hasValidAccessToken()) {
        this.oauthService.initLoginFlow(this.router.url);
      }
    });
    this.oauthService.events.pipe(first((e) => e.type === 'token_received')).subscribe(() => {
      this.eventUserComponentNotification.emit(this.claims);
      const deepLink = sessionStorage.getItem(DEEP_LINK_URL_KEY);
      sessionStorage.removeItem(DEEP_LINK_URL_KEY);
      this.router.navigate([deepLink]).then();
    });
  }

  login() {
    // Set the current url as the state. This will enable redirection after login.
    this.oauthService.initLoginFlow(Pages.HOME.path);
  }

  logout() {
    this.oauthService.logOut(true);
    return this.router.navigate([Pages.HOME.path]);
  }

  get roles(): Role[] {
    if (this.accessToken) {
      return this.decodeAccessToken().roles.filter((role) =>
        Object.values(Roles).includes(role as Role)
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
