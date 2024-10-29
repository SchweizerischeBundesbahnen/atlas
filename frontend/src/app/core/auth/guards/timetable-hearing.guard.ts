import { inject, Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
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

  canActivate() {
    return this.userService.onPermissionsLoaded().pipe(
      map(() => {
        if (!this.userService.loggedIn || !this.permissionService.mayAccessTimetableHearing()) {
          return this.router.parseUrl('/');
        }
        return true;
      }),
    );
  }
}

export const canActivateTimetableHearing: CanActivateFn = () => {
  return inject(TimetableHearingGuard).canActivate();
};
