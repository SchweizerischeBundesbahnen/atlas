import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  UserAdministrationService,
  UserInformationService,
  UserModel,
  UserPermissionCreateModel,
  UserPermissionModel,
} from '../../../api';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(
    private readonly userAdministrationService: UserAdministrationService,
    private readonly userInformationService: UserInformationService
  ) {}

  getUsers(page: number, size: number): Observable<{ users: UserModel[]; totalCount: number }> {
    return this.userAdministrationService.getUsers(page, size).pipe(
      map((value) => {
        return { users: value.objects!, totalCount: value.totalCount! };
      })
    );
  }

  getUser(userId: string): Observable<UserModel> {
    return this.userAdministrationService.getUser(userId);
  }

  searchUsers(searchQuery: string): Observable<UserModel[]> {
    return this.userInformationService.searchUsers(searchQuery);
  }

  hasUserPermissions(userId: string): Observable<boolean> {
    return this.getUser(userId).pipe(
      map((user) => {
        return this.getPermissionsFromUserModelAsArray(user).length > 0;
      })
    );
  }

  getPermissionsFromUserModelAsArray(user: UserModel): UserPermissionModel[] {
    return Array.from(user.permissions ?? []);
  }

  createUserPermission(userPermission: UserPermissionCreateModel): Observable<UserModel> {
    return this.userAdministrationService.createUserPermission(userPermission);
  }

  updateUserPermission(userPermission: UserPermissionCreateModel): Observable<UserModel> {
    return this.userAdministrationService.updateUserPermissions(userPermission);
  }
}
