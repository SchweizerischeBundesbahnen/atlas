import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
  AtlasGraphApiService,
  UserAdministrationService,
  UserModel,
  UserPermission,
} from '../../../api';
import { map, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(
    private readonly userAdministrationService: UserAdministrationService,
    private readonly atlasGraphApiService: AtlasGraphApiService
  ) {}

  getUsers(page: number, size: number): Observable<{ users: UserModel[]; totalCount: number }> {
    let totalCount = 0;
    return this.userAdministrationService.getUsers(page, size).pipe(
      switchMap((userIds) => {
        totalCount = userIds.totalCount ?? 0;
        if (userIds.objects && totalCount > 0) {
          return this.atlasGraphApiService.resolveUsers(userIds.objects) as Observable<UserModel[]>;
        }
        return of([]);
      }),
      map((value) => {
        return { users: value, totalCount };
      })
    );
  }

  getUserPermissions(userId: string): Observable<UserPermission[]> {
    return this.userAdministrationService.getUserPermissions(userId);
  }

  searchUsers(searchQuery: string): Observable<UserModel[]> {
    return this.atlasGraphApiService.searchUsers(searchQuery);
  }
}
