import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user/user.service';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard {
  constructor(
    private readonly userService: UserService,
    private readonly router: Router
  ) {}

  canActivate() {
    if (this.userService.isAdmin) {
      return true;
    }
    return this.router.parseUrl('/');
  }
}

export const adminUser = () => {
  return inject(AdminGuard).canActivate();
};
