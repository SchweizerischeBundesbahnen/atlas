import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ApplicationType } from '../../model/applicationType';
import { Permission } from '../../model/permission';
import { User } from '../../model/user';

@Injectable({
  providedIn: 'root',
})
export class UserAdministrationService {

  private readonly BASE_PATH = '/user-administration/v1/users';

  private readonly atlasApiService = inject(AtlasApiService);

  public updateUserPermission(userId: string, application: ApplicationType, permission: Permission): Observable<User> {
    this.atlasApiService.validateParams({ userId, application, permission });
    return this.atlasApiService.put(
      `${this.BASE_PATH}/${userId}/${application}`, permission);
  }

}
