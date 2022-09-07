import { Component, Input } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { BusinessOrganisation } from '../../../api';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
})
export class UserAdministrationApplicationConfigComponent {
  @Input() titleTranslationKey = '';

  readonly tableColumnDef: TableColumn<BusinessOrganisation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
      columnDef: 'number',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
      columnDef: 'said',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
      columnDef: 'abbreviation',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      columnDef: 'description',
    },
  ];
}
