import { Component, Inject, OnInit } from '@angular/core';
import {
  BusinessOrganisation,
  BusinessOrganisationsService,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyRelationsService,
} from '../../../../api';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { AuthService } from '../../../../core/auth/auth.service';
import { Role } from '../../../../core/auth/role';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import moment, { Moment } from 'moment';
import { TranslateService } from '@ngx-translate/core';
import { Language } from '../../../../core/components/language-switcher/language';
import { TableColumn } from '../../../../core/components/table/table-column';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../core/notification/notification.service';

type abbreviationType = 'abbreviationDe' | 'abbreviationFr' | 'abbreviationIt';
type descriptionType = 'descriptionDe' | 'descriptionFr' | 'descriptionIt';

@Component({
  templateUrl: './transport-company-detail.component.html',
  styleUrls: ['./transport-company-detail.component.scss'],
})
export class TransportCompanyDetailComponent implements OnInit {
  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private readonly businessOrganisationsService: BusinessOrganisationsService,
    private readonly transportCompanyRelationsService: TransportCompanyRelationsService,
    private readonly authService: AuthService,
    private readonly translateService: TranslateService,
    private readonly dialogService: DialogService,
    private readonly notificationService: NotificationService
  ) {}

  transportCompany!: TransportCompany;
  transportCompanyRelations!: TransportCompanyBoRelation[];
  businessOrganisationSearchResults: Observable<BusinessOrganisation[]> = of([]);
  selectedTransportCompanyRelationIndex = -1;

  editMode = false;

  totalCountOfFoundBusinessOrganisations = 0;
  readonly pageSizeForBusinessOrganisationSearch = 100;

  readonly transportCompanyRelationTableColumns: TableColumn<TransportCompanyBoRelation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
      valuePath: 'businessOrganisation.said',
      columnDef: 'said',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
      valuePath: 'businessOrganisation.organisationNumber',
      columnDef: 'organisationNumber',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
      valuePath: `businessOrganisation.${this.getCurrentLanguageKey('abbreviation')}`,
      columnDef: 'abbreviation',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      valuePath: `businessOrganisation.${this.getCurrentLanguageKey('description')}`,
      columnDef: 'description',
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      columnDef: 'validFrom',
      formatAsDate: true,
    },
    {
      headerTitle: 'COMMON.VALID_TO',
      value: 'validTo',
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

  readonly selectOption = (item: BusinessOrganisation) => {
    return `${item.organisationNumber} - ${item[this.getCurrentLanguageKey('abbreviation')]} - ${
      item[this.getCurrentLanguageKey('description')]
    }`;
  };

  ngOnInit() {
    this.transportCompany = this.dialogData.transportCompanyDetail[0];
    this.transportCompanyRelations = this.dialogData.transportCompanyDetail[1];
  }

  isAdmin(): boolean {
    return this.authService.hasRole(Role.BoAdmin);
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

  private cancelEdit(): void {
    this.editMode = false;
    this.form.reset();
  }

  getBusinessOrganisations(searchString: string): void {
    if (!searchString) return;
    this.businessOrganisationSearchResults = this.businessOrganisationsService
      .getAllBusinessOrganisations(
        [searchString],
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

  createRelation(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.transportCompanyRelationsService
      .createTransportCompanyRelation({
        transportCompanyId: this.transportCompany.id!,
        sboid: this.form.value.businessOrganisation!.sboid!,
        validFrom: moment(this.form.value.validFrom).toDate(),
        validTo: moment(this.form.value.validTo).toDate(),
      })
      .pipe(
        switchMap((savedRelation) =>
          this.reloadRelations().pipe(
            tap(() => {
              this.editMode = false;
              this.form.reset();
              this.selectedTransportCompanyRelationIndex = this.transportCompanyRelations.findIndex(
                (item) => item.id === savedRelation.id
              );
              this.notificationService.success('RELATION.ADD_SUCCESS_MSG');
            })
          )
        )
      )
      .subscribe();
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
              this.notificationService.success('RELATION.DELETE_SUCCESS_MSG');
            })
          )
        )
      )
      .subscribe();
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

  private getCurrentLanguageKey<keyType extends descriptionType | abbreviationType>(
    propertyName: 'description' | 'abbreviation'
  ): keyType {
    const selectedLanguage = this.translateService.currentLang ?? Language.DE;
    return `${propertyName}${selectedLanguage[0].toUpperCase()}${selectedLanguage[1]}` as keyType;
  }
}
