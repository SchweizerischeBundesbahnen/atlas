import { Component, inject, input, OnInit } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  Country,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../api';
import {
  ControlContainer,
  FormControl,
  FormGroup,
  NgForm,
  ReactiveFormsModule,
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
import { UserPermissionProviderService } from './user-permission-provider-service';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';

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
    ReactiveFormsModule,
  ],
})
export class ApplicationPermissionComponent implements OnInit {
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

  private readonly businessOrganisationService = inject(
    BusinessOrganisationService
  );
  private readonly boLanguageService = inject(
    BusinessOrganisationLanguageService
  );
  readonly boFormCtrlName = 'businessOrganisation';
  readonly businessOrganisationForm: FormGroup = new FormGroup({
    [this.boFormCtrlName]: new FormControl<BusinessOrganisation | null>(null),
  });
  currentBusinessOrganisations: BusinessOrganisation[] = [];
  selectedBusinessOrganisationIndex = -1;
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

  availableRoles: ApplicationRole[] = [];
  applicationConfig: ApplicationConfig = ApplicationPermissionConfig.get(
    ApplicationType.Ttfn
  );
  form!: FormGroup<ApplicationPermission>;
  permissionsForm!: FormGroup<PermissionRestriction>;
  currentRole!: ApplicationRole;
  currentRoleConfig!: RoleConfig;
  showAllSpecialPermissions = false;

  userPermissionProviderService = inject(UserPermissionProviderService);

  ngOnInit(): void {
    this.userPermissionProviderService.loadFormGroup(this.application());
    this.initializeView();
    this.userPermissionProviderService.formChanged.subscribe(() => {
      this.initializeView();
    });
  }

  private initializeView(): void {
    this.form = this.userPermissionProviderService.getCurrentForm()!;
    this.permissionsForm = this.form.controls.permissions;

    this.availableRoles = ApplicationPermissionConfig.getRoles(
      this.form.controls.application.value!
    );
    this.applicationConfig = ApplicationPermissionConfig.get(
      this.form.controls.application.value!
    );
    this.showAllSpecialPermissions =
      this.userPermissionProviderService.showAllSpecialPermissions();

    this.onRoleChanged(this.form.controls.role.value ?? ApplicationRole.Reader);

    this.permissionsForm.controls.sboidsRestrictions?.value?.forEach(
      (sboid) => {
        this.currentBusinessOrganisations = [];
        this.addBusinessOrganisationToCurrentTable(sboid);
      }
    );
  }

  removeBusinessOrganisation(): void {
    const sboids = this.form.controls.permissions.controls.sboidsRestrictions!;
    const updatedSboids = sboids.value!;
    updatedSboids.splice(this.selectedBusinessOrganisationIndex, 1);
    sboids.setValue(updatedSboids);
    sboids.markAsDirty();

    this.currentBusinessOrganisations =
      this.currentBusinessOrganisations.filter(
        (_, index) => index !== this.selectedBusinessOrganisationIndex
      );

    this.selectedBusinessOrganisationIndex = -1;
  }

  addBusinessOrganisation(): void {
    const sboid = this.businessOrganisationForm.get(this.boFormCtrlName)?.value;
    if (sboid) {
      this.addBusinessOrganisationToCurrentTable(sboid);

      const sboids =
        this.form.controls.permissions.controls.sboidsRestrictions!;
      const updatedSboids = sboids.value!;
      updatedSboids.push(sboid);
      sboids.setValue(updatedSboids);
      sboids.markAsDirty();
      this.businessOrganisationForm.reset();
    }
  }

  private addBusinessOrganisationToCurrentTable(sboid: string) {
    firstValueFrom(
      this.businessOrganisationService.getAllBusinessOrganisations(
        undefined,
        [sboid],
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
  }

  onRoleChanged(applicationRole: ApplicationRole) {
    this.currentRole = applicationRole;
    const availableConfig = this.applicationConfig.roles.find(
      (i) => i.role === applicationRole
    );
    if (!availableConfig) {
      throw new Error('Available Config not found for ' + applicationRole);
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
    return (
      this.currentRoleConfig.permissions.specialPermissions.includes(
        PermissionRestrictionType.BulkImport
      ) &&
      (this.showAllSpecialPermissions ||
        this.permissionsForm.controls.bulkImportRestriction?.value)
    );
  }

  get showInfoPlusTerminationVote() {
    return (
      this.currentRoleConfig.permissions.specialPermissions.includes(
        PermissionRestrictionType.InfoPlusTerminationVote
      ) &&
      (this.showAllSpecialPermissions ||
        this.permissionsForm.controls.infoPlusTerminationVote?.value)
    );
  }

  get showNovaTerminationVote() {
    return (
      this.currentRoleConfig.permissions.specialPermissions.includes(
        PermissionRestrictionType.NovaTerminationVote
      ) &&
      (this.showAllSpecialPermissions ||
        this.permissionsForm.controls.novaTerminationVote?.value)
    );
  }

  get showSpecialPermissions() {
    return (
      this.currentRoleConfig.permissions.specialPermissions.length > 0 &&
      (this.showAllSpecialPermissions ||
        this.showBulkImport ||
        this.showNovaTerminationVote ||
        this.showInfoPlusTerminationVote)
    );
  }

  onNovaToggle(value: boolean) {
    if (value) {
      this.form.controls.permissions.controls.infoPlusTerminationVote?.setValue(
        false
      );
    }
  }

  onInfoPlusToggle(value: boolean) {
    if (value) {
      this.form.controls.permissions.controls.novaTerminationVote?.setValue(
        false
      );
    }
  }
}
