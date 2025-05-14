import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService, OAuthStorage } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { jwtDecode } from 'jwt-decode';
import { Role } from './role';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { TokenUser, User } from './user/user';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly REQUESTED_ROUTE_STORAGE_KEY = 'requested_route';
  private readonly AUTH_STORAGE_ITEMS: string[] = [
    'access_token',
    'access_token_stored_at',
    'expires_at',
    'granted_scopes',
    'id_token',
    'id_token_claims_obj',
    'id_token_expires_at',
    'id_token_stored_at',
    'nonce',
    'PKCE_verifier',
    'refresh_token',
    'session_state',
  ];

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private userService: UserService,
    private pageService: PageService,
    private oauthStorage: OAuthStorage
  ) {
    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();
    this.oauthService.events.subscribe((event) => {
      if (event.type === 'token_refresh_error') {
        this.removeLoginTokenFromStorage();
      }
    });

    this.oauthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (!this.oauthService.hasValidAccessToken()) {
        this.removeLoginTokenFromStorage();
      }

      if (this.oauthService.getIdentityClaims()) {
        const user = this.userFromAccessToken();
        this.userService
          .setCurrentUserAndLoadPermissions(user)
          .subscribe(() => {
            this.pageService.addPagesBasedOnPermissions();

            const url = sessionStorage.getItem(
              this.REQUESTED_ROUTE_STORAGE_KEY
            );
            if (url) {
              this.router.navigateByUrl(url).then();
              sessionStorage.removeItem(this.REQUESTED_ROUTE_STORAGE_KEY);
            }
          });
      } else {
        this.userService.setToUnauthenticatedUser();
      }
    });
  }

  login() {
    sessionStorage.setItem(
      this.REQUESTED_ROUTE_STORAGE_KEY,
      location.pathname + location.search
    );
    // App will be reloaded after initCodeFlow
    this.oauthService.initCodeFlow(this.router.url);
  }

  logout() {
    this.oauthService.logOut();
  }

  userFromAccessToken(): User {
    const decodedUser: TokenUser = jwtDecode(
      this.oauthService.getAccessToken()
    );
    return {
      email: decodedUser.preferred_username,
      name: decodedUser.name,
      permissions: decodedUser.permissions,
      sbbuid: decodedUser.sbbuid,
      isAdmin: decodedUser.roles.includes(Role.AtlasAdmin),
    };
  }

  private removeLoginTokenFromStorage() {
    this.AUTH_STORAGE_ITEMS.forEach((item) =>
      this.oauthStorage.removeItem(item)
    );
  }
}
