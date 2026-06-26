import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { User } from '../../model/user';

@Injectable({
  providedIn: 'root',
})
export class TthUserAdministrationService {
  private readonly atlasApiService = inject(AtlasApiService);

  searchBoDossierAnsweringUsers(searchQuery: string): Observable<User[]> {
    const httpParams = this.atlasApiService.paramsOf({ searchQuery });
    return this.atlasApiService.get('/user-administration/v1/search-bo-dossier-answering-users', httpParams);
  }
}
