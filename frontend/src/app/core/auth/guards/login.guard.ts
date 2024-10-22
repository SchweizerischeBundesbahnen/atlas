import { inject, Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { UserService } from '../user/user.service';
import { map, skipWhile } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LoginGuard {
  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
  ) {}

  canActivate() {
    return this.userService.permissionsLoaded.pipe(
      skipWhile((loaded) => !loaded),
      map(() => {
        if (this.userService.loggedIn) {
          return true;
        }
        this.authService.login();
        return this.router.parseUrl('/');
      }),
    );
  }
}

export const loggedInUsers: CanActivateFn = () => {
  return inject(LoginGuard).canActivate();
};
