import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ApplicationType,
  ClientCredential,
  ClientCredentialAdministrationService,
  ClientCredentialPermissionCreate,
  Permission,
  PermissionRestrictionType,
  User,
  UserAdministrationService,
  UserInformationService,
  UserPermissionCreate,
} from '../../../api';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(
    private readonly userAdministrationService: UserAdministrationService,
    private readonly userInformationService: UserInformationService,
    private readonly clientCredentialAdministrationService: ClientCredentialAdministrationService,
  ) {}

  getUsers(
    page: number,
    size: number,
    sboids: Set<string> | undefined = undefined,
    type: PermissionRestrictionType | undefined = undefined,
    applicationTypes: Set<ApplicationType> | undefined = undefined,
  ): Observable<{ users: User[]; totalCount: number }> {
    return this.userAdministrationService.getUsers(sboids, type, applicationTypes, page, size).pipe(
      map((value) => {
        return { users: value.objects!, totalCount: value.totalCount! };
      }),
    );
  }

  getUser(userId: string): Observable<User> {
    return this.userAdministrationService.getUser(userId);
  }

  searchUsers(searchQuery: string): Observable<User[]> {
    return this.userInformationService.searchUsers(searchQuery);
  }

  hasUserPermissions(userId: string): Observable<boolean> {
    return this.getUser(userId).pipe(
      map((user) => {
        return this.getPermissionsFromUserModelAsArray(user).length > 0;
      }),
    );
  }

  getPermissionsFromUserModelAsArray(user: User | ClientCredential): Permission[] {
    return Array.from(user.permissions ?? []);
  }

  createUserPermission(userPermission: UserPermissionCreate): Observable<User> {
    return this.userAdministrationService.createUserPermission(userPermission);
  }

  updateUserPermission(userPermission: UserPermissionCreate): Observable<User> {
    return this.userAdministrationService.updateUserPermissions(userPermission);
  }

  getClientCredential(clientId: string): Observable<ClientCredential> {
    return this.clientCredentialAdministrationService.getClientCredential(clientId);
  }

  createClientCredentialPermission(
    permission: ClientCredentialPermissionCreate,
  ): Observable<ClientCredential> {
    return this.clientCredentialAdministrationService.createClientCredential(permission);
  }

  updateClientPermissions(
    permissions: ClientCredentialPermissionCreate,
  ): Observable<ClientCredential> {
    return this.clientCredentialAdministrationService.updateClientCredential(permissions);
  }
}
