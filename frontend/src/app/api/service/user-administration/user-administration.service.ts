import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ApplicationType } from '../../model/applicationType';
import { Permission } from '../../model/permission';
import { User } from '../../model/user';
import { PermissionRestrictionType } from '../../model/permissionRestrictionType';
import { UserPermissionCreate } from '../../model/userPermissionCreate';
import { UserDisplayName } from '../../model/userDisplayName';
import { ContainerUser } from '../../model/containerUser';

@Injectable({
  providedIn: 'root',
})
export class UserAdministrationService {

  private readonly USER_BASE_PATH = '/user-administration/v1/users';

  private readonly atlasApiService = inject(AtlasApiService);

  searchUsers(searchQuery: string): Observable<User[]> {
    const httpParams = this.atlasApiService.paramsOf({
      searchQuery,
    });
    return this.atlasApiService.get('/user-administration/v1/search', httpParams);
  }

  searchUsersInAtlas(
    searchQuery: string,
    applicationType: ApplicationType,
  ): Observable<User[]> {
    const httpParams = this.atlasApiService.paramsOf({
      searchQuery,
      applicationType,
    });
    return this.atlasApiService.get('/user-administration/v1/search-in-atlas', httpParams);
  }

  getUsers(
    page: number,
    size: number,
    permissionRestrictions: Set<string> | undefined = undefined,
    type: PermissionRestrictionType | undefined = undefined,
    applicationTypes: Set<ApplicationType> | undefined = undefined,
  ): Observable<ContainerUser> {
    const httpParams = this.atlasApiService.paramsOf({
      permissionRestrictions,
      type,
      applicationTypes,
      page,
      size,
    });
    return this.atlasApiService.get(this.USER_BASE_PATH, httpParams);
  }

  getCurrentUser(): Observable<User> {
    return this.atlasApiService.get(`${this.USER_BASE_PATH}/current`);
  }

  getUser(userId: string): Observable<User> {
    return this.atlasApiService.get(`${this.USER_BASE_PATH}/${userId}`);
  }

  getUserDisplayName(userId: string): Observable<UserDisplayName> {
    return this.atlasApiService.get(`${this.USER_BASE_PATH}/${userId}/displayname`);
  }

  createUserPermission(userPermission: UserPermissionCreate): Observable<User> {
    return this.atlasApiService.post(this.USER_BASE_PATH, userPermission);
  }

  public updateUserPermission(userId: string, application: ApplicationType, permission: Permission): Observable<User> {
    this.atlasApiService.validateParams({ userId, application, permission });
    return this.atlasApiService.put(
      `${this.USER_BASE_PATH}/${userId}/${application}`, permission);
  }

}
