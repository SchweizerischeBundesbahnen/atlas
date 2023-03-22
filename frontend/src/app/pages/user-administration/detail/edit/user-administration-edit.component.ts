import { Component, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../core/notification/notification.service';
import { MatDialogRef } from '@angular/material/dialog';
import { UserPermissionManager } from '../../service/user-permission-manager';
import { BusinessOrganisationsService } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../../service/user.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { User } from '../../../../api';
import { CreationEditionRecord } from '../../../../core/components/base-detail/user-edit-info/creation-edition-record';

@Component({
  selector: 'app-user-administration-edit',
  templateUrl: './user-administration-edit.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
})
export class UserAdministrationEditComponent implements OnInit {
  @Input() user?: User;
  editMode = false;
  saveEnabled = true;
  userRecord?: CreationEditionRecord;

  constructor(
    private readonly notificationService: NotificationService,
    private readonly boService: BusinessOrganisationsService,
    private readonly translatePipe: TranslatePipe,
    private readonly userService: UserService,
    private readonly dialogService: DialogService,
    readonly dialogRef: MatDialogRef<any>,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  ngOnInit() {
    const permissionsFromUserModelAsArray = this.userService.getPermissionsFromUserModelAsArray(
      this.user!
    );
    if (permissionsFromUserModelAsArray.length === 0) {
      this.user = undefined;
      return;
    }
    this.convertUserPermissionToRecord();

    this.userPermissionManager.setSbbUserId(this.user!.sbbUserId!);
    this.userPermissionManager.setPermissions(permissionsFromUserModelAsArray);
  }

  saveEdits(): void {
    this.saveEnabled = false;
    this.userPermissionManager.emitBoFormResetEvent();
    this.userPermissionManager.clearPermissionRestrictionsIfNotWriter();
    this.userService.updateUserPermission(this.userPermissionManager.userPermission).subscribe({
      next: (user: User) => {
        this.user = user;
        this.editMode = false;
        this.userPermissionManager.setPermissions(
          this.userService.getPermissionsFromUserModelAsArray(this.user)
        );
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');
        this.convertUserPermissionToRecord();
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
        this.userPermissionManager.emitBoFormResetEvent();
      }
    });
  }

  private convertUserPermissionToRecord(): void {
    const permissionsFromUserModelAsArray = this.userService.getPermissionsFromUserModelAsArray(
      this.user!
    );
    if (permissionsFromUserModelAsArray.length > 0) {
      this.userRecord = {
        editor: permissionsFromUserModelAsArray[0].editor,
        editionDate: permissionsFromUserModelAsArray[0].editionDate,
        creator: permissionsFromUserModelAsArray[0].creator,
        creationDate: permissionsFromUserModelAsArray[0].creationDate,
      };
    }
  }
}
