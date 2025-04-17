import { inject, Injectable } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { UserService } from '../user/user.service';
import { map } from 'rxjs/operators';

/**
 * Guards run before resolvers. This Guard can be used to make sure Resolvers wait for permissions to be loaded.
 */
@Injectable({ providedIn: 'root' })
export class PermissionsLoadedGuard {
  constructor(private readonly userService: UserService) {}

  canActivate() {
    return this.userService.onPermissionsLoaded().pipe(map(() => true));
  }
}

export const permissionsLoaded: CanActivateFn = () =>
  inject(PermissionsLoadedGuard).canActivate();
