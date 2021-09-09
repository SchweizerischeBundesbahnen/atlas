import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';

import { AuthService } from './auth.service';

// Avoid configuring the guard for the root path as this might cause a race condition.
@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    // eslint disable is only used for example purposes
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    next: ActivatedRouteSnapshot,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    // TODO: It's recommended to check for specific role instead of only loggedIn state
    return this.authService.loggedIn ? true : this.router.parseUrl('/');
  }
}
