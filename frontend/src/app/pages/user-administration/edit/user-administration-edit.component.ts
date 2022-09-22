import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/notification/notification.service';
import { MatDialogRef } from '@angular/material/dialog';
import { UserPermissionManager } from '../user-permission-manager';
import { BusinessOrganisationsService } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../service/user.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { UserModel } from '../../../api/model/userModel';

@Component({
  selector: 'app-user-administration-edit',
  templateUrl: './user-administration-edit.component.html',
  styleUrls: ['./user-administration-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserAdministrationEditComponent implements OnInit {
  constructor(
    private readonly notificationService: NotificationService,
    private readonly boService: BusinessOrganisationsService,
    private readonly translatePipe: TranslatePipe,
    private readonly userService: UserService,
    readonly dialogRef: MatDialogRef<any>,
    readonly dialogService: DialogService
  ) {}

  @Input() user?: UserModel;
  editMode = false;
  readonly userPermissionManager: UserPermissionManager = new UserPermissionManager(this.boService);

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
}
