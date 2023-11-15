import { Component, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../../../service/user.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { CreationEditionRecord } from '../../../../../core/components/base-detail/user-edit-info/creation-edition-record';
import moment from 'moment';
import { ActivatedRoute, Router } from '@angular/router';
import { BusinessOrganisationsService, User } from '../../../../../api';

@Component({
  selector: 'app-user-administration-edit',
  templateUrl: './user-administration-user-edit.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
})
export class UserAdministrationUserEditComponent implements OnInit {
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
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly userPermissionManager: UserPermissionManager,
  ) {}

  ngOnInit() {
    const permissionsFromUserModelAsArray = this.userService.getPermissionsFromUserModelAsArray(
      this.user!,
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
    this.userPermissionManager.clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser();
    this.userService.updateUserPermission(this.userPermissionManager.userPermission).subscribe({
      next: (user: User) => {
        this.user = user;
        this.editMode = false;
        this.userPermissionManager.setPermissions(
          this.userService.getPermissionsFromUserModelAsArray(this.user),
        );
        this.notificationService.success('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');
        this.convertUserPermissionToRecord();
      },
      error: () => (this.saveEnabled = true),
    });
  }

  cancelEdit(showDialog = true): void {
    if (!showDialog) {
      this.router.navigate(['..'], { relativeTo: this.route }).then();
      return;
    }
    this.dialogService.confirmLeave().subscribe((result) => {
      if (result) {
        this.editMode = false;
        this.userPermissionManager.setPermissions(
          this.userService.getPermissionsFromUserModelAsArray(this.user!),
        );
        this.userPermissionManager.emitBoFormResetEvent();
      }
    });
  }

  private convertUserPermissionToRecord(): void {
    const permissionsFromUserModelAsArray = this.userService.getPermissionsFromUserModelAsArray(
      this.user!,
    );
    if (permissionsFromUserModelAsArray.length > 0) {
      const firstCreated = permissionsFromUserModelAsArray.reduce((previousValue, currentValue) => {
        return moment(new Date(previousValue.creationDate!)).isBefore(
          moment(new Date(currentValue.creationDate!)),
        )
          ? previousValue
          : currentValue;
      });
      const lastEdited = permissionsFromUserModelAsArray.reduce((previousValue, currentValue) => {
        return moment(new Date(previousValue.editionDate!)).isAfter(
          moment(new Date(currentValue.editionDate!)),
        )
          ? previousValue
          : currentValue;
      });
      this.userRecord = {
        editor: lastEdited.editor,
        editionDate: lastEdited.editionDate,
        creator: firstCreated.creator,
        creationDate: firstCreated.creationDate,
      };
    }
  }
}
