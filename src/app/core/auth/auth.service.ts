import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { first } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { User } from '../components/user/user';
import { Pages } from '../../pages/pages';

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
    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();
    this.oauthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (!this.oauthService.hasValidIdToken() || !this.oauthService.hasValidAccessToken()) {
        this.oauthService.initLoginFlow(this.router.url);
      }
    });
    this.oauthService.events.pipe(first((e) => e.type === 'token_received')).subscribe(() => {
      this.eventUserComponentNotification.emit(this.claims);
      const state = decodeURIComponent(this.oauthService.state || '');
      if (state && state !== '/') {
        this.router.navigate([state]);
      }
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
}
