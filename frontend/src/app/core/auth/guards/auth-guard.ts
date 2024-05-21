import {inject, Injectable} from '@angular/core';
import {CanActivateFn, Router,} from '@angular/router';
import {AuthService} from '../auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate() {
    return this.authService.loggedIn
      ? true
      : (() => {
          this.authService.login();
          return this.router.parseUrl('/');
        })();
  }
}

export const loggedInUsers: CanActivateFn = () => {
  return inject(AuthGuard).canActivate();
};
