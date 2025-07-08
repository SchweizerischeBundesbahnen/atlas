import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { User } from '../../../../api';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';

@Injectable({
  providedIn: 'root',
})
export class UserAdministrationUserDetailResolver {
  constructor(
    private readonly userAdministrationService: UserAdministrationService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<User | undefined> {
    const sbbUserIdParam = route.paramMap.get('sbbUserId');
    if (sbbUserIdParam === 'add') {
      return of(undefined);
    }
    return this.userAdministrationService.getUser(sbbUserIdParam!).pipe(
      catchError(() => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path], {
            state: { notDismissSnackBar: true },
          })
          .then();
        return of(undefined);
      })
    );
  }
}

export const userResolver: ResolveFn<User | undefined> = (
  route: ActivatedRouteSnapshot
) => inject(UserAdministrationUserDetailResolver).resolve(route);
