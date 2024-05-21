import {inject, Injectable} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../auth.service';
import {Role} from '../role';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard {

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  canActivate() {
    if (this.authService.hasRole(Role.AtlasAdmin)) {
      return true;
    }
    return this.router.parseUrl('/');
  }
}

export const adminUsers: CanActivateFn = () => {
  return inject(AdminGuard).canActivate();
};
