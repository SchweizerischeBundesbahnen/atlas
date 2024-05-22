import {inject, Injectable} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {UserService} from "../user.service";

@Injectable({
  providedIn: 'root',
})
export class AdminGuard {

  constructor(private readonly userService: UserService, private readonly router: Router) {}

  canActivate() {
    if (this.userService.isAdmin) {
      return true;
    }
    return this.router.parseUrl('/');
  }
}

export const adminUsers: CanActivateFn = () => {
  return inject(AdminGuard).canActivate();
};
