import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ContainerCompany } from '../../model/containerCompany';
import { Company } from '../../model/company';

@Injectable({
  providedIn: 'root',
})
export class CompanyInternalService {

  private readonly BASE_PATH = '/business-organisation-directory/internal/companies';

  private readonly atlasApiService = inject(AtlasApiService);

  getCompanies(searchCriteria?: Array<string>, page?: number, size?: number, sort?: Array<string>): Observable<ContainerCompany> {
    const httpParams = this.atlasApiService.paramsOf({
      searchCriteria,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.BASE_PATH, httpParams);
  }

  getCompany(uic: number): Observable<Company> {
    this.atlasApiService.validateParams({ uic });
    return this.atlasApiService.get(`${this.BASE_PATH}/${uic}`);
  }

}
