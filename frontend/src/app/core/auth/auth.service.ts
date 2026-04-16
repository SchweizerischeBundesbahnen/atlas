import { inject, Injectable, InjectionToken } from '@angular/core';
import { LoginResponse, OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { TokenUser, User } from './user/user';
import { catchError, defaultIfEmpty, EMPTY, from, Observable, of, take } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Role } from './role';
import { Router } from '@angular/router';
import { map, switchMap } from 'rxjs/operators';

export const BC_TOKEN = new InjectionToken<(logoutFn: () => void) => BroadcastChannel>('BroadcastChannel creation', {
  providedIn: 'root',
  factory: () => (logoutFn: () => void) => {
    const bc = new BroadcastChannel('logout');
    bc.onmessage = () => {
      bc.close();
      logoutFn();
    };
    return bc;
  },
});

type UserData = {
  email: string;
  name: string;
  sbbuid: string;
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _bc?: BroadcastChannel;
  private readonly _tryLoginKey = 'tryLogin';
  private readonly _tryLoginValue = 'yes';
  private readonly _returnUrlKey = 'returnUrl';

  private readonly createBC = inject(BC_TOKEN);
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly userService = inject(UserService);
  private readonly pageService = inject(PageService);
  private readonly router = inject(Router);

  initAuth() {
    return this.oidcSecurityService.checkAuth().pipe(
      switchMap((loginResponse: LoginResponse): Observable<User> => {
        if (!loginResponse.isAuthenticated) {
          this.handleNotLoggedIn();
          return EMPTY;
        }
        this._bc = this.createBC(() => this.logout());
        localStorage.setItem(this._tryLoginKey, this._tryLoginValue);

        const user = this.buildUser(loginResponse.userData, loginResponse.accessToken);
        return this.userService.setCurrentUserAndLoadPermissions(user);
      }),
      switchMap(() => {
        this.pageService.addPagesBasedOnPermissions();
        return this.routeToReturnUrl();
      }),
      map((routingSuccess) => {
        if (!routingSuccess) {
          console.log('Error occurred during routing to returnUrl');
        }
        return true;
      }),
      catchError((error) => {
        console.log('Error occurred during authentication initialisation', error);
        return EMPTY;
      }),
      defaultIfEmpty(true),
      take(1)
    );
  }

  login() {
    sessionStorage.setItem(this._returnUrlKey, `${location.pathname}${location.search}${location.hash}`);
    this.oidcSecurityService.authorize();
  }

  logout() {
    this.oidcSecurityService
      .logoffAndRevokeTokens()
      .pipe(take(1))
      .subscribe(() => {
        localStorage.removeItem(this._tryLoginKey);
        this._bc?.postMessage(true);
      });
  }

  private handleNotLoggedIn() {
    if (this.shouldTryLogin()) {
      localStorage.removeItem(this._tryLoginKey);
      this.login();
      return;
    }
    this.userService.setToUnauthenticatedUser();
  }

  private buildUser(userData: UserData, accessToken: string): User {
    return {
      email: userData.email,
      name: userData.name,
      sbbuid: userData.sbbuid,
      isAdmin: this.isAdminFromToken(accessToken),
      permissions: [],
    };
  }

  private routeToReturnUrl() {
    const returnUrl = sessionStorage.getItem(this._returnUrlKey);
    if (returnUrl) {
      sessionStorage.removeItem(this._returnUrlKey);
      return from(this.router.navigateByUrl(returnUrl));
    }
    return of(true);
  }

  private shouldTryLogin() {
    return localStorage.getItem(this._tryLoginKey) === this._tryLoginValue;
  }

  private isAdminFromToken(token: string): boolean {
    const decodedUser: TokenUser = jwtDecode(token);
    return decodedUser.roles.includes(Role.AtlasAdmin);
  }
}
