import { Component } from '@angular/core';
import { BusinessOrganisationsService } from '../../../../api';
import { UserService } from '../../service/user.service';
import { UserPermissionManager } from '../../service/user-permission-manager';
import { NotificationService } from '../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { MatDialogRef } from '@angular/material/dialog';
import { User } from '../../../../api';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-user-administration-create',
  templateUrl: './user-administration-create.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
})
export class UserAdministrationCreateComponent {
  userLoaded?: User;
  userHasAlreadyPermissions = false;
  selectedUserHasNoUserId = false;
  saveEnabled = true;
  readonly userSearchForm: FormGroup = new FormGroup({
    userSearch: new FormControl<string | null>(null),
  });

  constructor(
    private readonly userService: UserService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dialogService: DialogService,
    private readonly dialogRef: MatDialogRef<any>,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  selectUser(user: User | undefined): void {
    this.selectedUserHasNoUserId = false;
    if (!user?.sbbUserId) {
      this.userHasAlreadyPermissions = false;
      this.userLoaded = undefined;
      if (user) {
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
    this.saveEnabled = false;
    this.userPermissionManager.setSbbUserId(this.userLoaded!.sbbUserId!);
    this.userPermissionManager.clearPermissionRestrictionsIfNotWriter();
    this.userService.createUserPermission(this.userPermissionManager.userPermission).subscribe({
      next: () => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path, this.userPermissionManager.getSbbUserId()], {
            relativeTo: this.route,
          })
          .then(() => this.notificationService.success('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'));
      },
      error: () => (this.saveEnabled = true),
    });
  }

  cancelCreation(showDialog = true): void {
    if (!showDialog) {
      this.dialogRef.close();
      return;
    }
    this.dialogService.confirmLeave().subscribe((result) => {
      if (result) {
        this.dialogRef.close();
      }
    });
  }
}
