import { Component, Input } from '@angular/core';
import { UserModel, UserPermissionModel } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { tap } from 'rxjs';
import { UserPermissionManager } from '../user-permission-manager';

interface ReadOnlyData<T> {
  translationKey: string;
  value: keyof T;
  formatValue?: <P>(value: P) => string;
  valueDisplayClass?: string;
}

@Component({
  selector: 'app-user-administration-detail',
  templateUrl: './user-administration-detail.component.html',
  styleUrls: ['./user-administration-detail.component.scss'],
})
export class UserAdministrationDetailComponent<T> {
  @Input() userLoaded?: UserModel;
  @Input() userHasAlreadyPermissions = false;
  @Input() applicationConfigManager!: UserPermissionManager;

  constructor(private readonly translatePipe: TranslatePipe) {}

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
