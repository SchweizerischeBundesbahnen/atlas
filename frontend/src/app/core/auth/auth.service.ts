import { Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { TokenUser, User } from './user/user';
import { combineLatest, EMPTY, of, take } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Role } from './role';
import { map, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private readonly oidcSecurityService: OidcSecurityService,
    private readonly userService: UserService,
    private readonly pageService: PageService
  ) {
    this.oidcSecurityService
      .getUserData()
      .pipe(
        switchMap((userData) => {
          console.log(userData);
          if (!userData) {
            this.userService.setToUnauthenticatedUser();
            return EMPTY;
          }

          return combineLatest([
            of(userData),
            this.oidcSecurityService.getAccessToken(),
          ]);
        }),
        switchMap(([userData, accessToken]) => {
          const user: User = {
            email: userData.email,
            name: userData.name,
            sbbuid: userData.sbbuid,
            isAdmin: this.isAdminFromToken(accessToken),
            permissions: [],
          };
          return this.userService.setCurrentUserAndLoadPermissions(user);
        }),
        map(() => {
          this.pageService.addPagesBasedOnPermissions();
        }),
        take(1)
      )
      .subscribe();
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logout() {
    this.oidcSecurityService
      .logoff()
      .subscribe((result) => console.log(result));
    // todo: mby use with revoke (check if it logouts from provider and redirects to home)
    //  check e2e tests
  }

  private isAdminFromToken(token: string): boolean {
    const decodedUser: TokenUser = jwtDecode(token);
    return decodedUser.roles.includes(Role.AtlasAdmin);
  }
}
