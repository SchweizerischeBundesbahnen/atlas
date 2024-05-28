import {inject, Injectable} from '@angular/core';
import {CanActivateFn, Router,} from '@angular/router';
import {AuthService} from '../auth.service';
import {UserService} from "../user/user.service";

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {

  constructor(private authService: AuthService,
              private userService: UserService,
              private router: Router) {}

  canActivate() {
    return this.userService.loggedIn
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
