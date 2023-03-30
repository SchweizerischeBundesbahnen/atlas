import { TableColumn } from '../../../../core/components/table/table-column';
import { ClientCredential } from '../../../../api';

export const tableColumns: TableColumn<ClientCredential>[] = [
  {
    headerTitle: 'USER_ADMIN.CLIENT_CREDENTIAL.CLIENT_ID',
    value: 'clientCredentialId',
  },
  {
    headerTitle: 'USER_ADMIN.CLIENT_CREDENTIAL.ALIAS',
    value: 'alias',
  },
  {
    headerTitle: 'USER_ADMIN.CLIENT_CREDENTIAL.COMMENT',
    value: 'comment',
  },
];
