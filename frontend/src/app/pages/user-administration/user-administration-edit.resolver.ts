import { Injectable } from '@angular/core';
import { Resolve, RouterStateSnapshot, ActivatedRouteSnapshot, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { UserService } from './service/user.service';
import { UserModel } from '../../api';
import { Pages } from '../pages';

@Injectable({
  providedIn: 'root',
})
export class UserAdministrationEditResolver implements Resolve<UserModel | undefined> {
  constructor(private readonly userService: UserService, private readonly router: Router) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<UserModel | undefined> {
    return this.userService.getUser(route.paramMap.get('sbbUserId')!).pipe(
      catchError(() => {
        this.router
          .navigate([Pages.LIDI.path], {
            state: { notDismissSnackBar: true },
          })
          .then();
        return of();
      })
    );
  }
}
