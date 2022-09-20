import { Component, Input } from '@angular/core';
import { ReadOnlyData } from './read-only-data';
import { UserModel } from '../../../api/model/userModel';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-user-administration-read-only-data',
  templateUrl: './user-administration-read-only-data.component.html',
  styleUrls: ['user-administration-read-only-data.component.scss'],
})
export class UserAdministrationReadOnlyDataComponent {
  @Input() data!: UserModel;

  constructor(private readonly translatePipe: TranslatePipe) {}

  readonly userModelConfig: ReadOnlyData<UserModel>[][] = [
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
