import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../auth.service';
import { Role } from '../role';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard implements CanActivate {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.authService.hasRole(Role.AtlasAdmin)) {
      return true;
    }
    return this.router.parseUrl('/');
  }
}
