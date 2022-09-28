import { Component, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../core/notification/notification.service';
import { MatDialogRef } from '@angular/material/dialog';
import { UserPermissionManager } from '../../service/user-permission-manager';
import { BusinessOrganisationsService } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../../service/user.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { UserModel } from '../../../../api/model/userModel';

@Component({
  selector: 'app-user-administration-edit',
  templateUrl: './user-administration-edit.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
})
export class UserAdministrationEditComponent implements OnInit {
  constructor(
    private readonly notificationService: NotificationService,
    private readonly boService: BusinessOrganisationsService,
    private readonly translatePipe: TranslatePipe,
    private readonly userService: UserService,
    private readonly dialogService: DialogService,
    readonly dialogRef: MatDialogRef<any>,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  @Input() user?: UserModel;
  editMode = false;
  saveEnabled = true;

  ngOnInit() {
    if (this.userService.getPermissionsFromUserModelAsArray(this.user!).length === 0) {
      this.user = undefined;
      return;
    }
    this.userPermissionManager.setSbbUserId(this.user!.sbbUserId!);
    this.userPermissionManager.setPermissions(
      this.userService.getPermissionsFromUserModelAsArray(this.user!)
    );
  }

  saveEdits(): void {
    this.saveEnabled = false;
    this.userPermissionManager.clearSboidsIfNotWriter();
    this.userService
      .updateUserPermission(this.userPermissionManager.getUserPermission())
      .subscribe({
        next: (user: UserModel) => {
          this.user = user;
          this.editMode = false;
          this.userPermissionManager.setPermissions(
            this.userService.getPermissionsFromUserModelAsArray(this.user)
          );
          this.notificationService.success('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');
        },
        error: () => (this.saveEnabled = true),
      });
  }

  cancelEdit(showDialog = true): void {
    if (!showDialog) {
      this.dialogRef.close();
      return;
    }
    this.dialogService.confirmLeave().subscribe((result) => {
      if (result) {
        this.editMode = false;
        this.userPermissionManager.setPermissions(
          this.userService.getPermissionsFromUserModelAsArray(this.user!)
        );
      }
    });
  }
}
