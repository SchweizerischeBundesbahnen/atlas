import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { BusinessOrganisationsService, BusinessOrganisationVersion } from '../../../../api';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class BusinessOrganisationDetailResolver {
  constructor(
    private readonly businessOrganisationsService: BusinessOrganisationsService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<BusinessOrganisationVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.businessOrganisationsService.getVersions(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.BODI.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => []),
          ),
        );
  }
}

export const businessOrganisationResolver: ResolveFn<Array<BusinessOrganisationVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(BusinessOrganisationDetailResolver).resolve(route);
