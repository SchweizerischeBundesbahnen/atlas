import { Component, Inject, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/notification/notification.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserPermissionManager } from '../user-permission-manager';
import { BusinessOrganisationsService, UserModel } from '../../../api';
import { ReadOnlyData } from '../../../core/components/read-only-data/read-only-data';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../service/user.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';

@Component({
  selector: 'app-edit',
  templateUrl: './user-administration-edit.component.html',
  styleUrls: ['./user-administration-edit.component.scss'],
})
export class UserAdministrationEditComponent implements OnInit {
  constructor(
    private readonly notificationService: NotificationService,
    private readonly boService: BusinessOrganisationsService,
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private readonly translatePipe: TranslatePipe,
    private readonly userService: UserService,
    readonly dialogRef: MatDialogRef<any>,
    readonly dialogService: DialogService
  ) {}

  readonly userPermissionManager: UserPermissionManager = new UserPermissionManager(this.boService);
  user?: UserModel;
  editMode = false;

  ngOnInit() {
    const user: UserModel = this.dialogData.user ?? ({} as UserModel);
    if (this.userService.getPermissionsFromUserModelAsArray(user).length === 0) {
      return;
    }
    this.user = user;
    this.userPermissionManager.setSbbUserId(this.user.sbbUserId!);
    this.userPermissionManager.setPermissions(
      this.userService.getPermissionsFromUserModelAsArray(this.user)
    );
  }

  getTitle(): string {
    if (this.user) {
      return `${this.user.firstName} ${this.user.lastName}`;
    }
    return this.translatePipe.transform('USER_ADMIN.NOT_FOUND');
  }

  saveEdits(): void {
    this.userPermissionManager.clearSboidsIfNotWriter();
    this.userService
      .updateUserPermission(this.userPermissionManager.getUserPermission())
      .subscribe((user) => {
        this.user = user;
        this.editMode = false;
        this.userPermissionManager.setPermissions(
          this.userService.getPermissionsFromUserModelAsArray(this.user)
        );
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');
      });
  }

  readonly readOnlyDataConfig: ReadOnlyData<UserModel>[][] = [
    [
      { translationKey: 'USER_ADMIN.FIRST_NAME', value: 'firstName' },
      { translationKey: 'USER_ADMIN.LAST_NAME', value: 'lastName' },
    ],
    [
      {
        translationKey: 'USER_ADMIN.MAIL',
        value: 'mail',
        valueDisplayClass: 'overflow-wrap-anywhere',
      },
      { translationKey: 'USER_ADMIN.USER_ID', value: 'sbbUserId' },
    ],
    [
      {
        translationKey: 'USER_ADMIN.ACCOUNT_STATUS',
        value: 'accountStatus',
        formatValue: (value) =>
          this.translatePipe.transform('USER_ADMIN.ACCOUNT_STATUS_TYPE.' + value),
      },
      { translationKey: 'USER_ADMIN.DISPLAY_NAME', value: 'displayName' },
    ],
  ];
}
