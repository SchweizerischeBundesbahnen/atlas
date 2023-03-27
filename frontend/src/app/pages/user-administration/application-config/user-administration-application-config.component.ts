import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  CantonPermissionRestrictionModel,
  PermissionRestrictionObject,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { UserPermissionManager } from '../service/user-permission-manager';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { Observable, of, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { Cantons } from '../../tth/overview/canton/Cantons';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserAdministrationApplicationConfigComponent implements OnInit, OnDestroy {
  @Input() application!: ApplicationType;
  @Input() readOnly = false;
  @Input() role: ApplicationRole = 'READER';

  boListener$: Observable<BusinessOrganisation[]> = of([]);

  availableOptions: ApplicationRole[] = [];
  selectedIndex = -1;

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

  private readonly boFormResetEventSubscription: Subscription;
  SWISS_CANTONS = Object.values(SwissCanton);
  SWISS_CANTONS_PREFIX_LABEL = 'TTH.CANTON.';
  cantonSelection: [SwissCanton] | undefined;

  constructor(
    private readonly boLanguageService: BusinessOrganisationLanguageService,
    readonly userPermissionManager: UserPermissionManager
  ) {
    this.boFormResetEventSubscription = userPermissionManager.boFormResetEvent$.subscribe(() =>
      this.businessOrganisationForm.reset()
    );
  }

  ngOnInit() {
    this.availableOptions = this.userPermissionManager.getAvailableApplicationRolesOfApplication(
      this.application
    );
    this.boListener$ = this.userPermissionManager.boOfApplicationsSubject$.pipe(
      map((bosOfApplications) => bosOfApplications[this.application])
    );
    this.cantonSelection = this.userPermissionManager.getRestrictionValues(
      this.userPermissionManager.getPermissionByApplication(this.application)
    ) as [SwissCanton];
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

  getCantonAbbreviation(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }

  cantonSelectionChanged($event: MatSelectChange) {
    const values = $event.value as SwissCanton[];
    const permissionRestriction = values.map((selection) => ({
      valueAsString: selection,
      type: PermissionRestrictionType.Canton,
    }));
    this.userPermissionManager.getPermissionByApplication(this.application).permissionRestrictions =
      permissionRestriction;
  }
}
