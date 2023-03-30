import { Component, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { MatDialogRef } from '@angular/material/dialog';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import {
  BusinessOrganisationsService,
  ClientCredential,
  ClientCredentialPermissionCreate,
  Permission,
} from '../../../../../api';
import { UserService } from '../../../service/user.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { CreationEditionRecord } from '../../../../../core/components/base-detail/user-edit-info/creation-edition-record';
import { ReadOnlyData } from '../../../components/read-only-data/read-only-data';

@Component({
  selector: 'app-client-credential-administration-edit',
  templateUrl: './user-administration-client-edit.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
})
export class UserAdministrationClientEditComponent implements OnInit {
  @Input() client!: ClientCredential;
  editMode = false;
  saveEnabled = true;
  record!: CreationEditionRecord;

  readonly readOnlyConfig: ReadOnlyData<ClientCredential>[][] = [
    [
      { translationKey: 'USER_ADMIN.CLIENT_CREDENTIAL.CLIENT_ID', value: 'clientCredentialId' },
      { translationKey: 'USER_ADMIN.CLIENT_CREDENTIAL.ALIAS', value: 'alias' },
    ],
  ];

  constructor(
    private readonly notificationService: NotificationService,
    private readonly userService: UserService,
    private readonly dialogService: DialogService,
    readonly dialogRef: MatDialogRef<any>,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  ngOnInit() {
    const permissionsFromUserModelAsArray = this.userService.getPermissionsFromUserModelAsArray(
      this.client
    );
    this.userPermissionManager.setPermissions(permissionsFromUserModelAsArray);
    this.convertPermissionToRecord(permissionsFromUserModelAsArray);
  }

  saveEdits(): void {
    this.saveEnabled = false;
    this.userPermissionManager.emitBoFormResetEvent();
    this.userPermissionManager.clearPermissionRestrictionsIfNotWriter();
    const managedPermissions = this.userPermissionManager.userPermission;
    const permissions = {
      ...this.client,
      ...managedPermissions,
    } as ClientCredentialPermissionCreate;
    this.userService.updateClientPermissions(permissions).subscribe({
      next: (client: ClientCredential) => {
        this.client = client;
        this.editMode = false;
        this.userPermissionManager.setPermissions(
          this.userService.getPermissionsFromUserModelAsArray(this.client)
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
          this.userService.getPermissionsFromUserModelAsArray(this.client!)
        );
        this.userPermissionManager.emitBoFormResetEvent();
      }
    });
  }

  private convertPermissionToRecord(permissions: Permission[]): void {
    if (permissions.length > 0) {
      this.record = {
        editor: permissions[0].editor,
        editionDate: permissions[0].editionDate,
        creator: permissions[0].creator,
        creationDate: permissions[0].creationDate,
      };
    }
  }
}
