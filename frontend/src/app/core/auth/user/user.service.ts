import { inject, Injectable } from '@angular/core';
import { Observable, ReplaySubject, Subject, take } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiConfigService } from '../../configuration/api-config.service';
import { Permission } from '../../../api';
import { User } from './user';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  currentUser?: User = undefined;
  readonly userChanged = new Subject<void>();
  private readonly permissionsLoaded = new ReplaySubject<void>(1);

  private readonly httpClient = inject(HttpClient);

  constructor(private apiConfigService: ApiConfigService) {}

  setCurrentUserAndLoadPermissions(user: User) {
    this.currentUser = user;
    this.apiConfigService.setToAuthenticatedUrl();
    this.userChanged.next();
    return this.loadPermissions();
  }

  setToUnauthenticatedUser() {
    this.currentUser = undefined;
    this.apiConfigService.setToUnauthenticatedUrl();
    this.userChanged.next();
    this.permissionsLoaded.next();
  }

  onPermissionsLoaded() {
    return this.permissionsLoaded.pipe(take(1));
  }

  get loggedIn() {
    return !!this.currentUser;
  }

  get permissions(): Permission[] {
    return this.currentUser?.permissions ?? [];
  }

  get isAdmin(): boolean {
    // Return true here if you want to be admin locally
    return this.currentUser?.isAdmin ?? false;
  }

  private loadPermissions(): Observable<User> {
    if (!this.loggedIn) {
      throw new Error('Can not load Permissions if not logged in');
    }
    return this.httpClient
      .get<User>(
        `${environment.atlasApiUrl}/user-administration/v1/users/current`,
        {
          headers: new HttpHeaders({ Accept: '*/*' }),
          responseType: 'json',
        }
      )
      .pipe(
        tap((response) => {
          this.currentUser!.permissions = response.permissions
            ? Array.from(response.permissions)
            : [];
          this.permissionsLoaded.next();
          this.userChanged.next();
        }),
        map(() => this.currentUser!)
      );
  }
}
