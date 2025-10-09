import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { BusinessOrganisationVersion } from '../../../../api';
import { Pages } from '../../../pages';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';

@Injectable({ providedIn: 'root' })
export class BusinessOrganisationDetailResolver {
  constructor(
    private readonly businessOrganisationService: BusinessOrganisationService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<BusinessOrganisationVersion[]> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.businessOrganisationService.getVersions(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.BODI.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => [])
          )
        );
  }
}

export const businessOrganisationResolver: ResolveFn<
  BusinessOrganisationVersion[]
> = (route: ActivatedRouteSnapshot) =>
  inject(BusinessOrganisationDetailResolver).resolve(route);
