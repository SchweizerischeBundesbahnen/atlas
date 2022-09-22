import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ApplicationRole, ApplicationType, BusinessOrganisation } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { UserPermissionManager } from '../user-permission-manager';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserAdministrationApplicationConfigComponent implements OnInit {
  @Input() applicationConfigManager!: UserPermissionManager;
  @Input() application!: ApplicationType;
  @Input() readOnly = false;

  constructor(private readonly boLanguageService: BusinessOrganisationLanguageService) {}

  ngOnInit() {
    this.selectedRole = this.applicationConfigManager.getCurrentRole(this.application);
    this.availableOptions = this.applicationConfigManager.getAvailableApplicationRolesOfApplication(
      this.application
    );
  }

  selectedRole: ApplicationRole = 'READER';
  availableOptions: ApplicationRole[] = [];
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
      value: this.boLanguageService.getCurrentLanguageAbbreviation(),
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      columnDef: 'description',
      value: this.boLanguageService.getCurrentLanguageDescription(),
    },
  ];

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
