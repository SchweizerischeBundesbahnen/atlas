import {inject, Injectable} from '@angular/core';
import {CanActivateFn} from '@angular/router';
import {filter} from "rxjs/operators";
import {UserService} from "../user/user.service";

/**
 * Guards run before resolvers. This Guard can be used to make sure Resolvers wait for permissions to be loaded.
 */
@Injectable({ providedIn: 'root' })
export class PermissionsLoadedGuard {

  constructor(
    private readonly userService: UserService,
  ) {}

  canActivate() {
    return this.userService.permissionsLoaded.pipe(filter(i => i));
  }
}

export const permissionsLoaded: CanActivateFn = () => inject(PermissionsLoadedGuard).canActivate();
