import { Component } from '@angular/core';
import { BusinessOrganisationsService, UserModel } from '../../../api';
import { UserService } from '../service/user.service';
import { UserPermissionManager } from '../user-permission-manager';
import { NotificationService } from '../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-user-administration-create',
  templateUrl: './user-administration-create.component.html',
  styleUrls: ['./user-administration-create.component.scss'],
})
export class UserAdministrationCreateComponent {
  constructor(
    private readonly userService: UserService,
    private readonly boService: BusinessOrganisationsService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  userLoaded?: UserModel;
  userHasAlreadyPermissions = false;
  selectedUserHasNoUserId = false;

  readonly userPermissionManager: UserPermissionManager = new UserPermissionManager(this.boService);

  selectUser(user: UserModel | undefined): void {
    this.selectedUserHasNoUserId = false;
    if (!user?.sbbUserId) {
      this.userHasAlreadyPermissions = false;
      this.userLoaded = undefined;
      if (user) {
        console.error('No UserId');
        this.selectedUserHasNoUserId = true;
      }
      return;
    }
    this.userService.getUser(user.sbbUserId).subscribe((user) => {
      this.userLoaded = user;
      this.userHasAlreadyPermissions =
        this.userService.getPermissionsFromUserModelAsArray(user).length > 0;
    });
  }

  createUser(): void {
    if (!this.userLoaded?.sbbUserId) {
      console.error('No UserId');
      return;
    }
    this.userPermissionManager.setSbbUserId(this.userLoaded.sbbUserId);
    this.userPermissionManager.clearSboidsIfNotWriter();
    this.userService
      .createUserPermission(this.userPermissionManager.getUserPermission())
      .subscribe(() => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path, this.userPermissionManager.getSbbUserId()], {
            relativeTo: this.route,
          })
          .then(() => this.notificationService.success('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'));
      });
  }
}
