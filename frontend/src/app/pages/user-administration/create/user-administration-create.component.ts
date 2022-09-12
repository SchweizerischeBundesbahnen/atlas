import { Component } from '@angular/core';
import { BusinessOrganisationsService, UserModel, UserPermissionCreateModel } from '../../../api';
import { UserService } from '../service/user.service';
import { tap } from 'rxjs';
import { UserPermissionManager } from '../user-permission-manager';

@Component({
  selector: 'app-user-administration-create',
  templateUrl: './user-administration-create.component.html',
  styleUrls: ['./user-administration-create.component.scss'],
})
export class UserAdministrationCreateComponent {
  constructor(
    private readonly userService: UserService,
    private readonly boService: BusinessOrganisationsService
  ) {}

  userLoaded?: UserModel = undefined;
  userHasAlreadyPermissions = false;

  userPermission: UserPermissionManager = new UserPermissionManager(this.boService);

  readonly userPermissionCreate: UserPermissionCreateModel = {
    sbbUserId: '',
    permissions: [
      {
        application: 'TTFN',
        role: 'WRITER',
        sboids: [],
      },
      {
        application: 'LIDI',
        role: 'WRITER',
        sboids: [],
      },
    ],
  };

  selectUser(user: UserModel): void {
    if (!user) {
      this.userHasAlreadyPermissions = false;
      this.userLoaded = undefined;
      return;
    }
    if (!user.sbbUserId) {
      console.error('No UserId');
      return;
    }
    this.userService
      .getUser(user.sbbUserId)
      .pipe(
        tap((user) => {
          this.userLoaded = user;
          this.userHasAlreadyPermissions =
            this.userService.getPermissionsFromUserModelAsArray(user).length > 0;
          this.userPermissionCreate.sbbUserId = user.sbbUserId!;
        })
      )
      .subscribe();
  }

  createUser(): void {
    if (!this.userLoaded) {
      console.error('Currently no User Loaded to save');
      return;
    }
    // create userPermission model
    console.log(this.userPermissionCreate);
    // this.userPermissionCreate.permissions[0].sboids = Array.from(
    //   this.userPermissionCreate.permissions[0].sboids
    // );
    // this.userPermissionCreate.permissions[1].sboids = Array.from(
    //   this.userPermissionCreate[1].sboids
    // );

    console.log(this.userPermissionCreate);

    // TODO: cannot send set

    this.userService.createUserPermission(this.userPermissionCreate).subscribe((user) => {
      // route to edit with created user
      console.log(user);
      // show success notification
    });
  }
}
