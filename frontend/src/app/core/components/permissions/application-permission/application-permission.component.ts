import {
  Component,
  inject,
  input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  Country,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../api';
import {
  ControlContainer,
  FormControl,
  FormGroup,
  NgForm,
} from '@angular/forms';
import {
  ApplicationPermission,
  PermissionRestriction,
} from '../form/application-permission-form-group';
import { AtlasLabelFieldComponent } from '../../../form-components/atlas-label-field/atlas-label-field.component';
import { AtlasSlideToggleComponent } from '../../../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { BusinessOrganisationSelectComponent } from '../../../form-components/bo-select/business-organisation-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { SelectComponent } from '../../../form-components/select/select.component';
import { Countries } from '../../../country/Countries';
import { Cantons } from '../../../cantons/Cantons';
import { TableColumn } from '../../table/table-column';
import { BusinessOrganisationLanguageService } from '../../../form-components/bo-select/business-organisation-language.service';
import { firstValueFrom } from 'rxjs';
import { RelationComponent } from '../../relation/relation.component';
import { AtlasSpacerComponent } from '../../spacer/atlas-spacer.component';
import {
  ApplicationConfig,
  ApplicationPermissionConfig,
  RoleConfig,
} from './application-permission.config';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'atlas-application-permission',
  templateUrl: './application-permission.component.html',
  styleUrls: ['./application-permission.component.scss'],
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    AtlasLabelFieldComponent,
    AtlasSlideToggleComponent,
    BusinessOrganisationSelectComponent,
    TranslatePipe,
    SelectComponent,
    RelationComponent,
    AtlasSpacerComponent,
    JsonPipe,
  ],
})
export class ApplicationPermissionComponent implements OnInit, OnChanges {
  readonly ApplicationType = ApplicationType;

  readonly getCountryEnum = Countries.getCountryEnum;
  readonly SWISS_COUNTRIES_PREFIX_LABEL = 'TTH.COUNTRY.';

  readonly COUNTRIES = this.filterAndSortCountries();

  private filterAndSortCountries(): Country[] {
    const sortedCountryArray: Country[] = [];
    sortedCountryArray.push(
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus
    );
    const filteredCountries = Countries.filteredCountries();
    filteredCountries.sort(Countries.compareFn);
    return sortedCountryArray.concat(filteredCountries);
  }

  readonly SWISS_CANTONS_PREFIX_LABEL = 'TTH.CANTON.';
  readonly getCantonAbbreviation = (canton: SwissCanton) =>
    Cantons.fromSwissCanton(canton)?.short;
  readonly SWISS_CANTONS = Object.values(SwissCanton);

  boService = inject(BusinessOrganisationsService);
  boLanguageService = inject(BusinessOrganisationLanguageService);
  readonly boFormCtrlName = 'businessOrganisation';
  readonly businessOrganisationForm: FormGroup = new FormGroup({
    [this.boFormCtrlName]: new FormControl<BusinessOrganisation | null>(null),
  });
  currentBusinessOrganisations: BusinessOrganisation[] = [];
  selectedIndex = -1;
  readonly tableColumnDef: TableColumn<BusinessOrganisation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
      columnDef: 'organisationNumber',
      value: 'organisationNumber',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SBOID',
      columnDef: 'sboid',
      value: 'sboid',
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

  application = input.required<ApplicationType>();
  form = input.required<FormGroup<ApplicationPermission>>();

  availableRoles: ApplicationRole[] = [];
  applicationConfig: ApplicationConfig = ApplicationPermissionConfig.get(
    ApplicationType.Ttfn
  );
  applicationForm!: FormGroup<ApplicationPermission>;
  permissionsForm!: FormGroup<PermissionRestriction>;
  currentRole!: ApplicationRole;
  currentRoleConfig!: RoleConfig;

  ngOnInit(): void {
    this.applicationForm = this.form();
    this.permissionsForm = this.applicationForm.controls.permissions;

    this.availableRoles = ApplicationPermissionConfig.getRoles(
      this.application()
    );
    this.applicationConfig = ApplicationPermissionConfig.get(
      this.application()
    );

    this.onRoleChanged(
      this.applicationForm.controls.role.value ?? ApplicationRole.Reader
    );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.application || changes.form) {
      this.ngOnInit();
    }
  }

  toggleBulkImport(value: boolean) {
    this.applicationForm.controls.permissions.controls.bulkImportRestriction!.setValue(
      value
    );
  }

  toggleNovaTermination(value: boolean) {
    this.applicationForm.controls.permissions.controls.novaTerminationVote!.setValue(
      value
    );
  }

  toggleInfoPlusTermination(value: boolean) {
    this.applicationForm.controls.permissions.controls.infoPlusTerminationVote!.setValue(
      value
    );
  }

  remove(): void {
    const sboids =
      this.applicationForm.controls.permissions.controls.sboidsRestrictions!;
    let updatedSboids = sboids.value!;
    updatedSboids.splice(this.selectedIndex, 1);
    sboids.setValue(updatedSboids);

    this.currentBusinessOrganisations =
      this.currentBusinessOrganisations.filter(
        (_, index) => index !== this.selectedIndex
      );

    this.selectedIndex = -1;
  }

  add(): void {
    const value = this.businessOrganisationForm.get(this.boFormCtrlName)?.value;
    if (value) {
      firstValueFrom(
        this.boService.getAllBusinessOrganisations(
          undefined,
          [value],
          undefined,
          undefined,
          0,
          1,
          ['sboid,ASC']
        )
      ).then((result) => {
        this.currentBusinessOrganisations = [
          ...this.currentBusinessOrganisations,
          result.objects![0],
        ];
      });

      const sboids =
        this.applicationForm.controls.permissions.controls.sboidsRestrictions!;
      const updatedSboids = sboids.value!;
      updatedSboids.push(value);
      sboids.setValue(updatedSboids);
      this.businessOrganisationForm.reset();
    }
  }

  onRoleChanged(applicationRole: ApplicationRole) {
    this.currentRole = applicationRole;
    const availableConfig = this.applicationConfig.roles.find(
      (i) => i.role === applicationRole
    );
    if (!availableConfig) {
      throw new Error('Available Config not found');
    }
    this.currentRoleConfig = availableConfig!;
  }

  get showBusinessOrganisationRestriction() {
    return this.currentRoleConfig.permissions.restrictions.includes(
      PermissionRestrictionType.BusinessOrganisation
    );
  }

  get showCountryRestriction() {
    return this.currentRoleConfig.permissions.restrictions.includes(
      PermissionRestrictionType.Country
    );
  }

  get showCantonRestriction() {
    return this.currentRoleConfig.permissions.restrictions.includes(
      PermissionRestrictionType.Canton
    );
  }

  get showBulkImport() {
    return this.currentRoleConfig.permissions.specialPermissions.includes(
      PermissionRestrictionType.BulkImport
    );
  }

  get showInfoPlusTerminationVote() {
    return this.currentRoleConfig.permissions.specialPermissions.includes(
      PermissionRestrictionType.InfoPlusTerminationVote
    );
  }

  get showNovaTerminationVote() {
    return this.currentRoleConfig.permissions.specialPermissions.includes(
      PermissionRestrictionType.NovaTerminationVote
    );
  }
}
