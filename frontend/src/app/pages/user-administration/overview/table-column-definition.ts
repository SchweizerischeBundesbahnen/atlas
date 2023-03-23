import { TableColumn } from '../../../core/components/table/table-column';
import { User } from '../../../api';

export const tableColumns: TableColumn<User>[] = [
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
    value: 'sbbUserId',
  },
  {
    headerTitle: 'USER_ADMIN.ACCOUNT_STATUS',
    value: 'accountStatus',
    translate: {
      withPrefix: 'USER_ADMIN.ACCOUNT_STATUS_TYPE.',
    },
  },
];
