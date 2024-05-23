import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {OAuthService} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';
import {User} from '../components/user/user';
import {Pages} from '../../pages/pages';
import {jwtDecode} from 'jwt-decode';
import {Role} from './role';
import {UserService} from "./user.service";
import {PageService} from "./page.service";

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly REQUESTED_ROUTE_STORAGE_KEY = 'requested_route';

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private userService: UserService,
    private pageService: PageService,
  ) {
    this.oauthService.configure(environment.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();

    this.oauthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (this.oauthService.getIdentityClaims()) {
        const user = this.userFromAccessToken();
        this.userService.setCurrentUserAndLoadPermissions(user).subscribe(() => {
          this.pageService.addPagesBasedOnPermissions();
        });

        this.router.navigateByUrl(sessionStorage.getItem(this.REQUESTED_ROUTE_STORAGE_KEY) ?? '').then();
      }
    });
  }

  login() {
    sessionStorage.setItem(this.REQUESTED_ROUTE_STORAGE_KEY, location.pathname + location.search);
    // App will be reloaded after initCodeFlow
    this.oauthService.initCodeFlow(this.router.url);
  }

  logout() {
    this.oauthService.logOut(true);

    this.userService.resetCurrentUser();
    this.pageService.resetPages();

    this.router.navigate([Pages.HOME.path]).then();
  }

  userFromAccessToken(): User {
    const decodedUser: User & { roles: string[] } = jwtDecode(this.oauthService.getAccessToken());
    return {
      ...decodedUser,
      isAdmin: decodedUser.roles.includes(Role.AtlasAdmin),
    }
  }
}
