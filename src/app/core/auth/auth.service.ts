import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { first } from 'rxjs/operators';
import { Location } from '@angular/common';

import { environment } from '../../../environments/environment';
import { User } from '../components/user/user';
import { Pages } from '../../pages/pages';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  eventUserComponentNotification: EventEmitter<User> = new EventEmitter<User>();
  // Promise that resolves once the login process has been completed.
  // This only works for forceful logins.
  private readonly initialized: Promise<unknown> | undefined;

  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  private accessTokenSubject = new BehaviorSubject<string>(null);
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  private userSubject = new BehaviorSubject<User>(null);

  get claims() {
    return this.oauthService.getIdentityClaims() as User;
  }

  get scopes() {
    return this.oauthService.getGrantedScopes() as string[];
  }

  get loggedIn() {
    return !!this.claims;
  }

  constructor(private oauthService: OAuthService, private router: Router, location: Location) {
    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();
    this.oauthService
      .loadDiscoveryDocumentAndTryLogin()
      // If the user should not be forcefully logged in (e.g. if you have pages, which can be
      // accessed anonymously), remove the .then and have a login functionality in the
      // template of the component injecting the AuthService which calls the login() method.
      .then(() => {
        if (!this.oauthService.hasValidIdToken() || !this.oauthService.hasValidAccessToken()) {
          this.oauthService.initLoginFlow(this.router.url);
        } else {
          console.log('user has valid token');
          this.accessTokenSubject.next(this.oauthService.getAccessToken());
          this.userSubject.next(this.claims);
        }
      });
    // Redirect the user to the url configured with state above or in a separate login call.
    this.oauthService.events.pipe(first((e) => e.type === 'token_received')).subscribe(() => {
      // This is required for RedHat SSO, since they encode the state.
      const state = decodeURIComponent(this.oauthService.state || '');
      if (state && state !== '/') {
        this.router.navigate([state]);
      } else {
        this.accessTokenSubject.next(this.oauthService.getAccessToken());
      }
    });
  }

  // Optional. Can be removed, if the user is forcefully logged in as defined above.
  login() {
    // Set the current url as the state. This will enable redirection after login.
    this.oauthService.initLoginFlow(Pages.HOME.path);
  }

  // Optional. Can be removed, if the user is forcefully logged in as defined above.
  logout() {
    // The parameter true is recommended here. If not set, after logout the user will
    // be redirected to the Logout page of Azure AD, which is only useful
    // if a device is shared by multiple users.
    this.oauthService.logOut(true);
    return this.router.navigate([Pages.HOME.path]);
  }

  async hasRole(role: string) {
    // Await the successful login of the user.
    await this.initialized;
    // Using indexOf for IE11 compatibility.
    return this.claims && Array.isArray(this.claims.roles) && this.claims.roles.indexOf(role) >= 0;
  }
}
