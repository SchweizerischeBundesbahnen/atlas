import { Injectable } from '@angular/core';
import { SearchByString } from '../form-components/atlas-search-select/search-by-string';
import { BusinessOrganisation, BusinessOrganisationsService } from '../../api';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BusinessOrganisationSearchService implements SearchByString<BusinessOrganisation> {
  constructor(private readonly businessOrganisationsService: BusinessOrganisationsService) {}

  searchByString(searchQuery: string): Observable<BusinessOrganisation[]> {
    return this.businessOrganisationsService
      .getAllBusinessOrganisations([searchQuery], [], undefined, undefined, undefined, undefined, [
        'organisationNumber,ASC',
      ])
      .pipe(map((value) => value.objects ?? []));
  }
}
