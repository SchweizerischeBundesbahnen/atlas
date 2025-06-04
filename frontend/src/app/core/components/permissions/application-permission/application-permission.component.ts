import { Component, inject, input, OnInit } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  Country,
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
  PermissionsForm,
} from '../form/permission-form-group';
import { AsyncPipe } from '@angular/common';
import { AtlasLabelFieldComponent } from '../../../form-components/atlas-label-field/atlas-label-field.component';
import { AtlasSlideToggleComponent } from '../../../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { BusinessOrganisationSelectComponent } from '../../../form-components/bo-select/business-organisation-select.component';
import { BULK_IMPORT_APPLICATIONS } from '../../../auth/permission/bulk-import-permission';
import { TranslatePipe } from '@ngx-translate/core';
import { SelectComponent } from '../../../form-components/select/select.component';
import { Countries } from '../../../country/Countries';
import { Cantons } from '../../../cantons/Cantons';
import { TableColumn } from '../../table/table-column';
import { BusinessOrganisationLanguageService } from '../../../form-components/bo-select/business-organisation-language.service';
import { BehaviorSubject, firstValueFrom, Observable, of } from 'rxjs';
import { RelationComponent } from '../../relation/relation.component';

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
  ],
})
export class ApplicationPermissionComponent implements OnInit {
  readonly bulkImportApplications = BULK_IMPORT_APPLICATIONS;
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

  private readonly availableApplicationRolesConfig: {
    [application in ApplicationType]: ApplicationRole[];
  } = {
    TTFN: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
    LIDI: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
    BODI: [ApplicationRole.Reader, ApplicationRole.Supervisor],
    TIMETABLE_HEARING: [
      ApplicationRole.Reader,
      ApplicationRole.ExplicitReader,
      ApplicationRole.Writer,
      ApplicationRole.Supervisor,
    ],
    SEPODI: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
    PRM: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
  };

  application = input.required<ApplicationType>();
  form = input.required<PermissionsForm>();

  applicationForm!: FormGroup<ApplicationPermission>;
  permissionsForm!: FormGroup<PermissionRestriction>;
  availableRoles: ApplicationRole[] = [];

  ngOnInit(): void {
    this.applicationForm = this.form().byApplication(this.application());
    this.permissionsForm = this.applicationForm.controls.permissions;
    this.availableRoles =
      this.availableApplicationRolesConfig[this.application()];
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

  get role() {
    return this.applicationForm.controls.role.value!;
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
}
