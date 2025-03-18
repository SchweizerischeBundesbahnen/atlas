import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {TableColumn} from '../../../../core/components/table/table-column';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  Country,
  CountryPermissionRestrictionModel,
  PermissionRestrictionType,
  SboidPermissionRestrictionModel,
  SwissCanton,
} from '../../../../api';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import {UserPermissionManager} from '../../service/user-permission-manager';
import {
  BusinessOrganisationLanguageService
} from '../../../../core/form-components/bo-select/business-organisation-language.service';
import {Observable, of, Subscription} from 'rxjs';
import {map} from 'rxjs/operators';
import {Cantons} from '../../../../core/cantons/Cantons';
import {MatSelectChange} from '@angular/material/select';
import {Countries} from '../../../../core/country/Countries';
import {BULK_IMPORT_APPLICATIONS} from "../../../../core/auth/permission/bulk-import-permission";
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasSlideToggleComponent } from '../../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { NgIf, AsyncPipe } from '@angular/common';
import { RelationComponent } from '../../../../core/components/relation/relation.component';
import { BusinessOrganisationSelectComponent } from '../../../../core/form-components/bo-select/business-organisation-select.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-user-administration-application-config',
    templateUrl: './user-administration-application-config.component.html',
    styleUrls: ['./user-administration-application-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [SelectComponent, AtlasLabelFieldComponent, AtlasSlideToggleComponent, NgIf, RelationComponent, BusinessOrganisationSelectComponent, ReactiveFormsModule, AsyncPipe, TranslatePipe]
})
export class UserAdministrationApplicationConfigComponent implements OnInit, OnDestroy {
  @Input() application!: ApplicationType;
  @Input() readOnly = false;
  @Input() role: ApplicationRole = 'READER';

  boListener$: Observable<BusinessOrganisation[]> = of([]);
  availableOptions: ApplicationRole[] = [];
  selectedIndex = -1;

  bulkImportApplications = BULK_IMPORT_APPLICATIONS;
  _bulkImportPermission = false;

  get bulkImportPermission(){
    return this._bulkImportPermission;
  }

  set bulkImportPermission(value: boolean) {
    const bulkImportPermission =
      this.userPermissionManager.getPermissionByApplication(this.application)
        .permissionRestrictions
        .find(i => i.type === PermissionRestrictionType.BulkImport);
    if (bulkImportPermission) {
      bulkImportPermission.valueAsString = String(value);
    } else {
      this.userPermissionManager.getPermissionByApplication(this.application)
        .permissionRestrictions.push({
        type: PermissionRestrictionType.BulkImport,
        valueAsString: String(value)
      });
    }

    this._bulkImportPermission = value;
  }

  public readonly getCountryEnum = Countries.getCountryEnum;
  readonly boFormCtrlName = 'businessOrganisation';
  readonly businessOrganisationForm: FormGroup = new FormGroup({
    [this.boFormCtrlName]: new FormControl<BusinessOrganisation | null>(null),
  });

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

  private filterAndSortCountries(): Country[] {
    const sortedCountryArray: Country[] = [];
    sortedCountryArray.push(
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus,
    );
    const filteredCountries = Countries.filteredCountries();
    filteredCountries.sort(Countries.compareFn);
    return sortedCountryArray.concat(filteredCountries);
  }

  private readonly boFormResetEventSubscription: Subscription;
  SWISS_CANTONS = Object.values(SwissCanton);
  COUNTRIES = this.filterAndSortCountries();
  SWISS_CANTONS_PREFIX_LABEL = 'TTH.CANTON.';
  SWISS_COUNTRIES_PREFIX_LABEL = 'TTH.COUNTRY.';
  cantonSelection: [SwissCanton] | undefined;
  countrySelection: [Country] | undefined;

  constructor(
    private readonly boLanguageService: BusinessOrganisationLanguageService,
    readonly userPermissionManager: UserPermissionManager,
  ) {
    this.boFormResetEventSubscription = userPermissionManager.boFormResetEvent$.subscribe(() =>
      this.businessOrganisationForm.reset(),
    );
  }

  resetCountries() {
    this.countrySelection = this.userPermissionManager.getRestrictionValues(
      this.userPermissionManager.getPermissionByApplication(this.application),
    ) as [Country];
  }

  ngOnInit() {
    this.availableOptions = this.userPermissionManager.getAvailableApplicationRolesOfApplication(
      this.application,
    );
    this.bulkImportPermission = this.userPermissionManager.getPermissionByApplication(this.application)
      .permissionRestrictions.find(i => i.type === PermissionRestrictionType.BulkImport)?.valueAsString === "true";

    this.boListener$ = this.userPermissionManager.boOfApplicationsSubject$.pipe(
      map((bosOfApplications) => bosOfApplications[this.application]),
    );
    this.cantonSelection = this.userPermissionManager.getRestrictionValues(
      this.userPermissionManager.getPermissionByApplication(this.application),
    ) as [SwissCanton];
    this.resetCountries();
  }

  ngOnDestroy() {
    this.boFormResetEventSubscription.unsubscribe();
  }

  add(): void {
    const value = this.businessOrganisationForm.get(this.boFormCtrlName)?.value;
    if (value) {
      this.userPermissionManager.addSboidToPermission(this.application, value);
      this.businessOrganisationForm.reset();
    }
  }

  remove(): void {
    this.userPermissionManager.removeSboidFromPermission(this.application, this.selectedIndex);
    this.selectedIndex = -1;
  }

  readonly getCantonAbbreviation = (canton: SwissCanton) => Cantons.fromSwissCanton(canton)?.short;

  cantonSelectionChanged($event: MatSelectChange) {
    const values = $event.value as SwissCanton[];
    const permissionRestriction = values.map((selection) => ({
      valueAsString: selection,
      type: PermissionRestrictionType.Canton,
    }));
    this.userPermissionManager.getPermissionByApplication(this.application).permissionRestrictions =
      permissionRestriction;
  }

  countrySelectionChanged($event: MatSelectChange) {
    const values = $event.value as Country[];
    const countryPermissionRestrictions = values
      .filter((value) => value !== undefined)
      .map((selection) => ({
        valueAsString: selection,
        type: PermissionRestrictionType.Country,
      }));
    const businessPermissionRestrictions = this.userPermissionManager
      .getPermissionByApplication(this.application)
      .permissionRestrictions.filter(
        (sboid) => sboid.type === PermissionRestrictionType.BusinessOrganisation,
      );
    const role = this.userPermissionManager.getPermissionByApplication(this.application).role;
    this.setSboidAndCountryPermissions(
      businessPermissionRestrictions,
      countryPermissionRestrictions,
      role,
      this.application,
    );
  }

  setSboidAndCountryPermissions(
    businessPermissionRestrictions: SboidPermissionRestrictionModel[],
    countryPermissionRestrictions: CountryPermissionRestrictionModel[],
    role: ApplicationRole,
    application: ApplicationType,
  ) {
    this.userPermissionManager.setPermissions([
      {
        application: application,
        role: role,
        permissionRestrictions: businessPermissionRestrictions,
      },
      {
        application: application,
        role: role,
        permissionRestrictions: countryPermissionRestrictions,
      },
    ]);
  }
}
