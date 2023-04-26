import { inject, Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { filter, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingGuard {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  canActivate() {
    return this.authService.permissionsLoaded.pipe(
      filter((loaded) => loaded),
      map(() => {
        if (this.authService.mayAccessTimetableHearing()) {
          return true;
        } else {
          return this.router.parseUrl('/');
        }
      })
    );
  }
}

export const canActivateTimetableHearing: CanActivateFn = () => {
  return inject(TimetableHearingGuard).canActivate();
};
