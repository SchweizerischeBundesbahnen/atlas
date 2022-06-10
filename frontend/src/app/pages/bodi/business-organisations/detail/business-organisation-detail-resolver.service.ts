import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { BusinessOrganisationsService, BusinessOrganisationVersion } from '../../../../api';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class BusinessOrganisationDetailResolver
  implements Resolve<Array<BusinessOrganisationVersion>>
{
  constructor(
    private readonly businessOrganisationsService: BusinessOrganisationsService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<BusinessOrganisationVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.businessOrganisationsService.getBusinessOrganisationVersions(idParameter).pipe(
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
