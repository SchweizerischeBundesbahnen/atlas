import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {map, tap} from "rxjs/operators";
import {ApiConfigService} from "../../configuration/api-config.service";
import {Permission, UserAdministrationService} from "../../../api";
import {User} from "./user";

@Injectable({
  providedIn: 'root',
})
export class UserService {

  readonly userChanged = new Subject<void>();
  readonly permissionsLoaded = new BehaviorSubject(false);

  currentUser?: User = undefined;

  constructor(private userAdministrationService: UserAdministrationService, private apiConfigService: ApiConfigService) {
  }


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
    this.permissionsLoaded.next(true);
  }

  get loggedIn() {
    return !!this.currentUser;
  }

  loadPermissions(): Observable<User> {
    if (!this.loggedIn) {
      throw new Error("Can not load Permissions if not logged in");
    }
    this.permissionsLoaded.next(false);
    return this.userAdministrationService.getCurrentUser().pipe(
      tap((response) => {
        this.currentUser!.permissions = response.permissions ? Array.from(response.permissions) : [];
        this.permissionsLoaded.next(true);
        this.userChanged.next();
      }),
      map(() => this.currentUser!)
    );
  }

  get permissions(): Permission[] {
    return this.currentUser?.permissions ?? [];
  }

  get isAdmin(): boolean {
    // Return true here if you want to be admin locally
    return this.currentUser?.isAdmin ?? false;
  }

}
