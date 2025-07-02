import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { ClientCredential } from '../../../../api';
import { ClientCredentialAdministrationService } from '../../../../api/service/user-administration/client-credential-administration.service';

@Injectable({
  providedIn: 'root',
})
export class ClientCredentialAdministrationResolver {
  clientCredentialAdministrationService = inject(
    ClientCredentialAdministrationService
  );
  router = inject(Router);

  resolve(route: ActivatedRouteSnapshot): Observable<ClientCredential> {
    const clientIdParam = route.paramMap.get('clientId');
    if (clientIdParam === 'add') {
      return of({});
    }
    return this.clientCredentialAdministrationService
      .getClientCredential(clientIdParam!)
      .pipe(
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

export const clientCredentialResolver: ResolveFn<ClientCredential> = (
  route: ActivatedRouteSnapshot
) => inject(ClientCredentialAdministrationResolver).resolve(route);
