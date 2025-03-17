import {Component, OnInit} from '@angular/core';
import {
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyRelationsService,
} from '../../../../api';
import {Observable, of} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {DateRangeValidator} from '../../../../core/validation/date-range/date-range-validator';
import moment, {Moment} from 'moment';
import {TableColumn} from '../../../../core/components/table/table-column';
import {DialogService} from '../../../../core/components/dialog/dialog.service';
import {NotificationService} from '../../../../core/notification/notification.service';
import {
  BusinessOrganisationLanguageService
} from '../../../../core/form-components/bo-select/business-organisation-language.service';
import {TransportCompanyFormGroup} from './transport-company-form-group';
import {ActivatedRoute} from '@angular/router';
import {DetailFormComponent} from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import {PermissionService} from "../../../../core/auth/permission/permission.service";

@Component({
    templateUrl: './transport-company-detail.component.html',
    styleUrls: ['./transport-company-detail.component.scss'],
    standalone: false
})
export class TransportCompanyDetailComponent implements OnInit, DetailFormComponent {
  transportCompany!: TransportCompany;
  transportFormGroup!: FormGroup<TransportCompanyFormGroup>;
  transportCompanyRelations!: TransportCompanyBoRelation[];
  businessOrganisationSearchResults: Observable<BusinessOrganisation[]> = of([]);
  selectedTransportCompanyRelationIndex = -1;
  editMode = false;
  totalCountOfFoundBusinessOrganisations = 0;
  isUpdateRelationSelected = false;
  relationId = 0;
  readonly pageSizeForBusinessOrganisationSearch = 100;
  readonly transportCompanyRelationTableColumns: TableColumn<TransportCompanyBoRelation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SBOID',
      valuePath: 'businessOrganisation.sboid',
      columnDef: 'sboid',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
      valuePath: 'businessOrganisation.organisationNumber',
      columnDef: 'organisationNumber',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
      valuePath: `businessOrganisation.${this.getCurrentLanguageAbbreviation()}`,
      columnDef: 'abbreviation',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      valuePath: `businessOrganisation.${this.getCurrentLanguageDescription()}`,
      columnDef: 'description',
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      valuePath: 'validFrom',
      columnDef: 'validFrom',
      formatAsDate: true,
    },
    {
      headerTitle: 'COMMON.VALID_TO',
      value: 'validTo',
      valuePath: 'validTo',
      columnDef: 'validTo',
      formatAsDate: true,
    },
  ];

  readonly form = new FormGroup(
    {
      businessOrganisation: new FormControl<BusinessOrganisation | null>(null, [
        Validators.required,
      ]),
      validFrom: new FormControl<Moment | null>(null),
      validTo: new FormControl<Moment | null>(null),
    },
    [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
  );

  constructor(
    private readonly businessOrganisationsService: BusinessOrganisationsService,
    private readonly transportCompanyRelationsService: TransportCompanyRelationsService,
    private readonly permissionService: PermissionService,
    private readonly businessOrganisationLanguageService: BusinessOrganisationLanguageService,
    private readonly dialogService: DialogService,
    private readonly notificationService: NotificationService,
    private readonly activatedRoute: ActivatedRoute
  ) {}

  readonly selectOption = (item: BusinessOrganisation) => {
    return `${item.organisationNumber} - ${item[this.getCurrentLanguageAbbreviation()]} - ${
      item[this.getCurrentLanguageDescription()]
    }`;
  };

  ngOnInit() {
    this.transportCompany = this.activatedRoute.snapshot.data.transportCompanyDetail[0];
    this.transportCompanyRelations = this.activatedRoute.snapshot.data.transportCompanyDetail[1];
    this.transportFormGroup = new FormGroup<TransportCompanyFormGroup>({
      id: new FormControl({ value: this.transportCompany.id, disabled: true }),
      number: new FormControl({ value: this.transportCompany.number, disabled: true }),
      abbreviation: new FormControl({ value: this.transportCompany.abbreviation, disabled: true }),
      description: new FormControl({ value: this.transportCompany.description, disabled: true }),
      enterpriseId: new FormControl({ value: this.transportCompany.enterpriseId, disabled: true }),
      businessRegisterName: new FormControl({
        value: this.transportCompany.businessRegisterName,
        disabled: true,
      }),
      businessRegisterNumber: new FormControl({
        value: this.transportCompany.businessRegisterNumber,
        disabled: true,
      }),
      comment: new FormControl({ value: this.transportCompany.comment, disabled: true }),
    });
  }

  mayCreate(): boolean {
    return this.permissionService.hasPermissionsToCreate(ApplicationType.Bodi);
  }

  leaveEditMode(): void {
    if (!this.form.dirty) {
      this.cancelEdit();
      return;
    }

    this.dialogService
      .confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      })
      .subscribe((result) => {
        if (result) {
          this.cancelEdit();
        }
      });
  }

  getBusinessOrganisations(searchString: string): void {
    if (!searchString) return;
    this.businessOrganisationSearchResults = this.businessOrganisationsService
      .getAllBusinessOrganisations(
        [searchString],
        undefined,
        undefined,
        undefined,
        undefined,
        this.pageSizeForBusinessOrganisationSearch
      )
      .pipe(
        map((value) => {
          this.totalCountOfFoundBusinessOrganisations = value.totalCount!;
          return value.objects ?? [];
        })
      );
  }

  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const validFrom = moment(this.form.value.validFrom).toDate();
    const validTo = moment(this.form.value.validTo).toDate();

    const save = this.isUpdateRelationSelected
      ? this.transportCompanyRelationsService.updateTransportCompanyRelation({
          id: this.relationId,
          validFrom,
          validTo,
        })
      : this.transportCompanyRelationsService.createTransportCompanyRelation({
          transportCompanyId: this.transportCompany.id!,
          sboid: this.form.value.businessOrganisation!.sboid!,
          validFrom,
          validTo,
        });

    save
      .pipe(
        switchMap(() => this.reloadRelations()),
        tap(() => {
          this.editMode = false;
          this.form.reset();
          const successMsg = this.isUpdateRelationSelected
            ? 'RELATION.UPDATE_SUCCESS_MSG'
            : 'RELATION.ADD_SUCCESS_MSG';
          this.notificationService.success(successMsg);
          this.isUpdateRelationSelected = false;
          this.selectedTransportCompanyRelationIndex = -1;
        })
      )
      .subscribe();
  }

  updateRelation() {
    this.transportCompanyRelationsService
      .getTransportCompanyRelations(this.transportCompany.id!)
      .subscribe((relations) => {
        const foundRelation = relations.find(
          (_, index) => index === this.selectedTransportCompanyRelationIndex
        )!;
        this.form.setValue({
          businessOrganisation: foundRelation.businessOrganisation!,
          validFrom: moment(foundRelation.validFrom),
          validTo: moment(foundRelation.validTo),
        });
        this.relationId = foundRelation.id!;
        this.isUpdateRelationSelected = true;
      });
  }

  deleteRelation(): void {
    this.transportCompanyRelationsService
      .deleteTransportCompanyRelation(
        this.transportCompanyRelations[this.selectedTransportCompanyRelationIndex].id!
      )
      .pipe(
        switchMap(() =>
          this.reloadRelations().pipe(
            tap(() => {
              this.selectedTransportCompanyRelationIndex = -1;
              this.isUpdateRelationSelected = false;
              this.notificationService.success('RELATION.DELETE_SUCCESS_MSG');
            })
          )
        )
      )
      .subscribe();
  }

  private cancelEdit(): void {
    this.editMode = false;
    this.isUpdateRelationSelected = false;
    this.form.reset();
  }

  private reloadRelations(): Observable<TransportCompanyBoRelation[]> {
    return this.transportCompanyRelationsService
      .getTransportCompanyRelations(this.transportCompany.id!)
      .pipe(
        tap(
          (transportCompanyRelations) =>
            (this.transportCompanyRelations = transportCompanyRelations)
        )
      );
  }

  private getCurrentLanguageAbbreviation() {
    return this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation();
  }

  private getCurrentLanguageDescription() {
    return this.businessOrganisationLanguageService.getCurrentLanguageDescription();
  }

}
