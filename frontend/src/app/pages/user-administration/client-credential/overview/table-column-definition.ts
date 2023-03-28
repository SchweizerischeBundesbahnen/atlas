import { TableColumn } from '../../../../core/components/table/table-column';
import { ClientCredential, User } from '../../../../api';

export const tableColumns: TableColumn<ClientCredential>[] = [
  {
    headerTitle: 'USER_ADMIN.LAST_NAME',
    value: 'clientCredentialId',
  },
  {
    headerTitle: 'USER_ADMIN.FIRST_NAME',
    value: 'alias',
  },
  {
    headerTitle: 'USER_ADMIN.MAIL',
    value: 'comment',
  },
];
