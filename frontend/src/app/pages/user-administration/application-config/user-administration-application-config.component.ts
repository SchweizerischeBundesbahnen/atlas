import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ApplicationRole, ApplicationType, BusinessOrganisation } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { UserPermissionManager } from '../user-permission-manager';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserAdministrationApplicationConfigComponent implements OnInit {
  @Input() application!: ApplicationType;
  @Input() readOnly = false;
  @Input() role: ApplicationRole = 'READER';

  boListener$: Observable<BusinessOrganisation[]> = of([]);

  constructor(
    private readonly boLanguageService: BusinessOrganisationLanguageService,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  ngOnInit() {
    this.availableOptions = this.userPermissionManager.getAvailableApplicationRolesOfApplication(
      this.application
    );
    this.boListener$ = this.userPermissionManager.boOfApplicationsSubject$.pipe(
      map((bosOfApplications) => bosOfApplications[this.application])
    );
  }

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
      this.userPermissionManager.addSboidToPermission(this.application, value);
    }
  }

  remove(): void {
    this.userPermissionManager.removeSboidFromPermission(this.application, this.selectedIndex);
    this.selectedIndex = -1;
  }
}
