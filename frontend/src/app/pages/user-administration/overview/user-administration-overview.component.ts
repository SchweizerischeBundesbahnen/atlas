import { Component } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
  styleUrls: ['./user-administration-overview.component.scss'],
})
export class UserAdministrationOverviewComponent {
  users: any[] = [];
  readonly tableColumns: TableColumn<any>[] = [
    {
      headerTitle: 'USER_ADMIN.LAST_NAME',
      value: 'lastName',
    },
    {
      headerTitle: 'USER_ADMIN.FIRST_NAME',
      value: 'firstName',
    },
    {
      headerTitle: 'USER_ADMIN.MAIL',
      value: 'mail',
    },
    {
      headerTitle: 'USER_ADMIN.USER_ID',
      value: 'userId',
    },
    {
      headerTitle: 'USER_ADMIN.ACCOUNT_STATUS',
      value: 'accountStatus',
    },
  ];
}

// TODO: create new rbt admin group
