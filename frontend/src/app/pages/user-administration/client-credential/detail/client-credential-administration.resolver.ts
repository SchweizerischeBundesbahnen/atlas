import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { UserService } from '../../service/user.service';
import { Pages } from '../../../pages';
import { ClientCredential } from '../../../../api';

@Injectable({
  providedIn: 'root',
})
export class ClientCredentialAdministrationResolver {
  constructor(
    private readonly userService: UserService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ClientCredential> {
    const clientIdParam = route.paramMap.get('clientId');
    if (clientIdParam === 'add') {
      return of({});
    }
    return this.userService.getClientCredential(clientIdParam!).pipe(
      catchError(() => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path], {
            state: { notDismissSnackBar: true },
          })
          .then();
        return of({});
      }),
    );
  }
}

export const clientCredentialResolver: ResolveFn<ClientCredential> = (
  route: ActivatedRouteSnapshot,
) => inject(ClientCredentialAdministrationResolver).resolve(route);
