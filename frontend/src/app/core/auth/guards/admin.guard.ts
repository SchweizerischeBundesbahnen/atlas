import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user/user.service';
import { map } from 'rxjs/operators';
import { LoginGuard } from './login.guard';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard {
  constructor(
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly loginGuard: LoginGuard
  ) {}

  canActivate() {
    return this.loginGuard.canActivate().pipe(
      map(() => {
        if (this.userService.isAdmin) {
          return true;
        }
        return this.router.parseUrl('/');
      })
    );
  }
}

export const adminUser = () => {
  return inject(AdminGuard).canActivate();
};
