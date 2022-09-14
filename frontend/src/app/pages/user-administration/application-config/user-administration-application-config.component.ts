import { Component, Input, ViewChild } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ApplicationRole, ApplicationType, BusinessOrganisation } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { RelationComponent } from '../../../core/components/relation/relation.component';
import { UserPermissionManager } from '../user-permission-manager';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
})
export class UserAdministrationApplicationConfigComponent {
  @ViewChild(RelationComponent) relationComponent!: RelationComponent<BusinessOrganisation>;

  @Input() applicationConfigManager!: UserPermissionManager;
  @Input() application!: ApplicationType;

  currentRecords: BusinessOrganisation[] = [];
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

  getRoleOptions(): ApplicationRole[] {
    return Object.values(ApplicationRole);
  }

  isCurrentRoleWriter(): boolean {
    return this.applicationConfigManager.getCurrentRole(this.application) === 'WRITER';
  }

  addBusinessOrganisation(): void {
    const value = this.businessOrganisationForm.get('businessOrganisation')?.value;
    if (!value) {
      return;
    }

    this.applicationConfigManager
      .addSboidToPermission(this.application, value)
      .subscribe((value) => {
        if (value) {
          this.currentRecords.push(value);
          this.relationComponent.table.renderRows();
        }
      });
  }

  removeBusinessOrganisation(): void {
    this.applicationConfigManager.removeSboidFromPermission(this.application, this.selectedIndex);
    this.currentRecords.splice(this.selectedIndex, 1);
    this.relationComponent.table.renderRows();
    this.selectedIndex = -1;
  }
}
