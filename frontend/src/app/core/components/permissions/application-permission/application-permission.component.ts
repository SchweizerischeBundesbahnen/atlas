import { ChangeDetectionStrategy, Component, computed, effect, inject, input, OnInit, signal } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  Country,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../api';
import { form } from '@angular/forms/signals';
import { AtlasSlideToggleComponent } from '../../../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { AtlasBoSelectComponent } from '../../../form-components/atlas-bo-select/atlas-bo-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSelectComponent, AtlasLabelFieldComponent } from '@atlas/form';
import { Countries } from '../../../country/Countries';
import { Cantons } from '../../../cantons/Cantons';
import { TableColumn } from '../../table/table-column';
import { BusinessOrganisationLanguageService } from '../../../../pages/bodi/business-organisations/shared/business-organisation-language.service';
import { forkJoin } from 'rxjs';
import { RelationComponent } from '../../relation/relation.component';
import { AtlasSpacerComponent } from '../../spacer/atlas-spacer.component';
import { ApplicationPermissionConfig } from './application-permission.config';
import { UserPermissionProviderService } from './user-permission-provider-service';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';
import { ContainerBusinessOrganisation } from '../../../../api/model/containerBusinessOrganisation';

@Component({
  selector: 'atlas-application-permission',
  templateUrl: './application-permission.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./application-permission.component.scss'],
  imports: [
    AtlasLabelFieldComponent,
    AtlasSlideToggleComponent,
    AtlasBoSelectComponent,
    TranslatePipe,
    AtlasSelectComponent,
    RelationComponent,
    AtlasSpacerComponent,
  ],
})
export class ApplicationPermissionComponent implements OnInit {
  private readonly businessOrganisationService = inject(BusinessOrganisationService);
  private readonly boLanguageService = inject(BusinessOrganisationLanguageService);
  private readonly userPermissionProviderService = inject(UserPermissionProviderService);

  readonly application = input.required<ApplicationType>();
  readonly form = computed(() => {
    const currentForm = this.userPermissionProviderService.currentForm();
    return currentForm?.().value().application === this.application() ? currentForm : undefined;
  });
  readonly availableRoles = computed(() => ApplicationPermissionConfig.getRoles(this.application()));
  readonly currentRole = computed(() => this.form()?.role().value() ?? ApplicationRole.Reader);
  readonly currentRoleConfig = computed(() => {
    const roleConfig = ApplicationPermissionConfig.getByRole(this.application(), this.currentRole());
    if (!roleConfig) {
      throw new Error(`Unsupported role ${this.currentRole()} for application ${this.application()}`);
    }
    return roleConfig;
  });
  readonly showAllSpecialPermissions = this.userPermissionProviderService.showAllSpecialPermissions();
  readonly currentBusinessOrganisations = signal<BusinessOrganisation[]>([]);
  readonly selectedBusinessOrganisationIndex = signal(-1);
  readonly lookupError = signal(false);

  readonly getCountryEnum = Countries.getCountryEnum;
  readonly SWISS_COUNTRIES_PREFIX_LABEL = 'TTH.COUNTRY.';
  readonly COUNTRIES = this.filterAndSortCountries();
  readonly SWISS_CANTONS_PREFIX_LABEL = 'TTH.CANTON.';
  readonly getCantonAbbreviation = (canton: SwissCanton) => Cantons.fromSwissCanton(canton)?.short;
  readonly SWISS_CANTONS = Object.values(SwissCanton);
  readonly businessOrganisationForm = form(
    signal<{ businessOrganisation: BusinessOrganisation | string | null }>({
      businessOrganisation: null,
    })
  );
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

  readonly showBusinessOrganisationRestriction = computed(() =>
    this.hasRestriction(PermissionRestrictionType.BusinessOrganisation)
  );
  readonly showCountryRestriction = computed(() => this.hasRestriction(PermissionRestrictionType.Country));
  readonly showCantonRestriction = computed(() => this.hasRestriction(PermissionRestrictionType.Canton));
  readonly showBulkImport = computed(() =>
    this.hasSpecialPermission(
      PermissionRestrictionType.BulkImport,
      this.form()?.permissions.bulkImportRestriction().value()
    )
  );
  readonly showInfoPlusTerminationVote = computed(() =>
    this.hasSpecialPermission(
      PermissionRestrictionType.InfoPlusTerminationVote,
      this.form()?.permissions.infoPlusTerminationVote().value()
    )
  );
  readonly showNovaTerminationVote = computed(() =>
    this.hasSpecialPermission(
      PermissionRestrictionType.NovaTerminationVote,
      this.form()?.permissions.novaTerminationVote().value()
    )
  );
  readonly showTransportCompanyDossierAnswer = computed(() =>
    this.hasSpecialPermission(
      PermissionRestrictionType.TransportCompanyDossierAnswer,
      this.form()?.permissions.transportCompanyDossierAnswer().value()
    )
  );
  readonly showSpecialPermissions = computed(
    () =>
      this.showBulkImport() ||
      this.showInfoPlusTerminationVote() ||
      this.showNovaTerminationVote() ||
      this.showTransportCompanyDossierAnswer()
  );

  constructor() {
    effect((onCleanup) => {
      const permissionForm = this.form();
      const sboids = permissionForm?.permissions.sboidsRestrictions().value() ?? [];
      this.currentBusinessOrganisations.set([]);
      this.selectedBusinessOrganisationIndex.set(-1);
      this.lookupError.set(false);
      if (sboids.length === 0) {
        return;
      }

      const subscription = forkJoin(
        sboids.map((sboid) =>
          this.businessOrganisationService.getAllBusinessOrganisations(undefined, [sboid], undefined, undefined, 0, 1, [
            'sboid,ASC',
          ])
        )
      ).subscribe({
        next: (results) => this.updateBusinessOrganisations(results, sboids),
        error: () => this.lookupError.set(true),
      });
      onCleanup(() => subscription.unsubscribe());
    });
  }

  private updateBusinessOrganisations(results: ContainerBusinessOrganisation[], sboids: string[]): void {
    const organisations = results.map((result, index) =>
      result.objects?.find((organisation) => organisation.sboid === sboids[index])
    );
    if (organisations.some((organisation) => !organisation)) {
      this.lookupError.set(true);
    }
    this.currentBusinessOrganisations.set(organisations.filter((organisation) => !!organisation));
  }

  ngOnInit(): void {
    this.userPermissionProviderService.loadForm(this.application());
  }

  private filterAndSortCountries(): Country[] {
    const sortedCountryArray: Country[] = [
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus,
    ];
    const filteredCountries = Countries.filteredCountries();
    filteredCountries.sort(Countries.compareFn);
    return sortedCountryArray.concat(filteredCountries);
  }

  removeBusinessOrganisation(): void {
    const sboid = this.currentBusinessOrganisations()[this.selectedBusinessOrganisationIndex()]?.sboid;
    const field = this.form()?.permissions.sboidsRestrictions();
    if (!sboid || !field) {
      return;
    }
    field.value.set(field.value().filter((value) => value !== sboid));
    field.markAsDirty();
    this.currentBusinessOrganisations.update((organisations) =>
      organisations.filter((organisation) => organisation.sboid !== sboid)
    );
    this.selectedBusinessOrganisationIndex.set(-1);
  }

  addBusinessOrganisation(): void {
    const selection = this.businessOrganisationForm.businessOrganisation().value();
    const sboid = typeof selection === 'string' ? selection : selection?.sboid;
    const field = this.form()?.permissions.sboidsRestrictions();
    if (!sboid || !field || field.value().includes(sboid)) {
      return;
    }
    field.value.set([...field.value(), sboid]);
    field.markAsDirty();
    this.businessOrganisationForm().reset({ businessOrganisation: null });
  }

  onNovaToggle(value: boolean): void {
    if (value) {
      this.form()?.permissions.infoPlusTerminationVote().value.set(false);
    }
  }

  onInfoPlusToggle(value: boolean): void {
    if (value) {
      this.form()?.permissions.novaTerminationVote().value.set(false);
    }
  }

  private hasRestriction(type: PermissionRestrictionType): boolean {
    return this.currentRoleConfig().permissions.restrictions.includes(type);
  }

  private hasSpecialPermission(type: PermissionRestrictionType, selected: boolean | undefined): boolean {
    return (
      this.currentRoleConfig().permissions.specialPermissions.includes(type) &&
      (this.showAllSpecialPermissions || !!selected)
    );
  }
}
