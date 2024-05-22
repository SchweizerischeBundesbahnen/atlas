import {Injectable} from '@angular/core';
import {User} from "../components/user/user";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Permission, UserAdministrationService} from "../../api";
import {map, tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class UserService {

  readonly userChanged = new Subject<void>();
  readonly permissionsLoaded = new BehaviorSubject(false);

  currentUser?: User = undefined;

  constructor(private userAdministrationService: UserAdministrationService) {
  }


  setCurrentUserAndLoadPermissions(user: User) {
    this.currentUser = user;
    this.userChanged.next();
    return this.loadPermissions();
  }

  resetCurrentUser() {
    this.currentUser = undefined;
    this.userChanged.next();
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
