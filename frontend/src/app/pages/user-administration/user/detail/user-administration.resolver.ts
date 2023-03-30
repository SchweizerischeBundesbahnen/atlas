import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { UserService } from '../../service/user.service';
import { Pages } from '../../../pages';
import { User } from '../../../../api';

@Injectable({
  providedIn: 'root',
})
export class UserAdministrationResolver implements Resolve<User> {
  constructor(private readonly userService: UserService, private readonly router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<User> {
    const sbbUserIdParam = route.paramMap.get('sbbUserId');
    if (sbbUserIdParam === 'add') {
      return of({});
    }
    return this.userService.getUser(sbbUserIdParam!).pipe(
      catchError(() => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path], {
            state: { notDismissSnackBar: true },
          })
          .then();
        return of({});
      })
    );
  }
}
