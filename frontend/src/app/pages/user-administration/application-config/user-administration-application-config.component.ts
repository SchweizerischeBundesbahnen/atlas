import { Component, Input } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ApplicationType, BusinessOrganisation } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { UserPermissionManager } from '../user-permission-manager';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
})
export class UserAdministrationApplicationConfigComponent {
  @Input() applicationConfigManager!: UserPermissionManager;
  @Input() application!: ApplicationType;
  @Input() readOnly = false;

  selectedIndex = -1;

  readonly businessOrganisationForm: FormGroup = new FormGroup({
    businessOrganisation: new FormControl<BusinessOrganisation | null>(null),
  });

  readonly tableColumnDef: TableColumn<BusinessOrganisation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
      columnDef: 'organisationNumber',
      value: 'organisationNumber',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
      columnDef: 'said',
      value: 'said',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
      columnDef: 'abbreviation',
      value: 'abbreviationDe',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      columnDef: 'description',
      value: 'descriptionDe',
    },
  ];

  isCurrentRoleWriter(): boolean {
    return this.applicationConfigManager.getCurrentRole(this.application) === 'WRITER';
  }

  add(): void {
    const value = this.businessOrganisationForm.get('businessOrganisation')?.value;
    if (value) {
      this.applicationConfigManager.addSboidToPermission(this.application, value);
    }
  }

  remove(): void {
    this.applicationConfigManager.removeSboidFromPermission(this.application, this.selectedIndex);
    this.selectedIndex = -1;
  }
}
