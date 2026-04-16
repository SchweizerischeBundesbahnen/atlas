import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { TthDossier } from '../../../../api/model/tthDossier';

@Injectable({ providedIn: 'root' })
export class DossierDetailResolver {
  private readonly dossierInternalService = inject(DossierInternalService);
  private readonly router = inject(Router);

  resolve(route: ActivatedRouteSnapshot): Observable<TthDossier | undefined> {
    const idParameter = route.paramMap.get('id') || '0';
    return idParameter === 'add'
      ? of(undefined)
      : this.dossierInternalService.getDossier(parseInt(idParameter)).pipe(
          catchError(() => {
            const hearingStatus = route.data['hearingStatus'];
            this.router
              .navigate([Pages.TTH.path, route.paramMap.get('canton')?.toLowerCase(), hearingStatus.toLowerCase()], {
                state: { notDismissSnackBar: true },
              })
              .then();
            return of(undefined);
          })
        );
  }
}

export const dossierResolver: ResolveFn<TthDossier | undefined> = (route: ActivatedRouteSnapshot) =>
  inject(DossierDetailResolver).resolve(route);
