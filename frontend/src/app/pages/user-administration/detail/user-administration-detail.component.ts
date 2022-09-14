import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { UserModel } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { UserPermissionManager } from '../user-permission-manager';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { MatDialogRef } from '@angular/material/dialog';
import { ReadOnlyData } from '../../../core/components/read-only-data/read-only-data';

@Component({
  selector: 'app-user-administration-detail',
  templateUrl: './user-administration-detail.component.html',
  styleUrls: ['./user-administration-detail.component.scss'],
})
export class UserAdministrationDetailComponent {
  @Input() userLoaded?: UserModel;
  @Input() userHasAlreadyPermissions = false;
  @Input() applicationConfigManager!: UserPermissionManager;

  @ContentChild('footerButtons') footerButtons!: TemplateRef<any>;

  constructor(
    private readonly translatePipe: TranslatePipe,
    private readonly dialogService: DialogService,
    private readonly dialogRef: MatDialogRef<any>
  ) {}

  readonly confirmCancel = (showDialog: boolean) => {
    if (!showDialog) {
      this.dialogRef.close();
      return;
    }
    this.dialogService
      .confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      })
      .subscribe((result) => {
        if (result) {
          this.dialogRef.close();
        } else {
          this.dialogService.closeConfirmDialog();
        }
      });
  };

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
