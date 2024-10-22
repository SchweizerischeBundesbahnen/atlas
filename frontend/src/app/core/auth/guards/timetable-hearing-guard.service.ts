import { inject, Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { PermissionService } from '../permission/permission.service';
import { UserService } from '../user/user.service';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingGuard {
  constructor(
    private permissionService: PermissionService,
    private userService: UserService,
    private router: Router,
  ) {}

  // todo: make async
  canActivate() {
    if (!this.userService.loggedIn) {
      return this.router.parseUrl('/');
    }
    return this.userService.permissionsLoaded.pipe(
      filter((loaded) => loaded),
      map(() => {
        if (this.permissionService.mayAccessTimetableHearing()) {
          return true;
        } else {
          return this.router.parseUrl('/');
        }
      }),
    );
  }
}

export const canActivateTimetableHearing: CanActivateFn = () => {
  return inject(TimetableHearingGuard).canActivate();
};
