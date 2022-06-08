import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, of } from 'rxjs';
import { BusinessOrganisationsService, BusinessOrganisationVersion } from '../../../../api';

@Injectable({ providedIn: 'root' })
export class BusinessOrganisationDetailResolver
  implements Resolve<Array<BusinessOrganisationVersion>>
{
  constructor(private businessOrganisationsService: BusinessOrganisationsService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<BusinessOrganisationVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.businessOrganisationsService.getBusinessOrganisationVersions(idParameter);
  }
}
