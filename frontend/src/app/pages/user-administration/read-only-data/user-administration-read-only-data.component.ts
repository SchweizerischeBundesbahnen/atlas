import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ReadOnlyData } from './read-only-data';
import { TranslatePipe } from '@ngx-translate/core';
import { Data } from './data';

@Component({
  selector: 'app-user-administration-read-only-data',
  templateUrl: './user-administration-read-only-data.component.html',
  styleUrls: ['user-administration-read-only-data.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserAdministrationReadOnlyDataComponent<T extends Data> {
  @Input() data!: T;

  constructor(private readonly translatePipe: TranslatePipe) {}

  readonly userModelConfig: ReadOnlyData<T>[][] = [
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
        formatValue: (value) => {
          return this.translatePipe.transform('USER_ADMIN.ACCOUNT_STATUS_TYPE.' + value);
        },
      },
      { translationKey: 'USER_ADMIN.DISPLAY_NAME', value: 'displayName' },
    ],
  ];
}
