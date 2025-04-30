import { inject, Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Pages } from '../pages';
import { ApplicationType } from '../../api';
import { PermissionService } from '../../core/auth/permission/permission.service';

@Injectable({ providedIn: 'root' })
export class CanActivateServicePointCreationGuard {
  constructor(
    private readonly permissionService: PermissionService,
    private readonly router: Router
  ) {}

  canActivate() {
    if (this.permissionService.hasPermissionsToCreate(ApplicationType.Sepodi)) {
      return true;
    }
    return this.router.parseUrl(Pages.SEPODI.path);
  }
}

export const canCreateServicePoint: CanActivateFn = () =>
  inject(CanActivateServicePointCreationGuard).canActivate();
