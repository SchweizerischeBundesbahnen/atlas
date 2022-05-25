import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { BusinessOrganisationsService, BusinessOrganisationVersion } from '../../../../api';
import { NotificationService } from '../../../../core/notification/notification.service';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class BusinessOrganisationDetailResolver
  implements Resolve<Array<BusinessOrganisationVersion>>
{
  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<BusinessOrganisationVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.businessOrganisationsService.getBusinessOrganisationVersions(idParameter).pipe(
          catchError((error) => {
            this.router.navigate([Pages.BODI.path]).then();
            this.notificationService.error(error);
            return EMPTY;
          })
        );
  }
}
