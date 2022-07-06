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
import { TableColumn } from '../../../../core/components/table/table-column';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import moment, { Moment } from 'moment';
import { TranslateService } from '@ngx-translate/core';
import { Language } from '../../../../core/components/language-switcher/language';

type abbreviationType = 'abbreviationDe' | 'abbreviationFr' | 'abbreviationIt';

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
    private readonly translateService: TranslateService
  ) {}

  transportCompany!: TransportCompany;
  transportCompanyRelations!: TransportCompanyBoRelation[];
  businessOrganisationSearchResults: Observable<BusinessOrganisation[]> = of([]);

  totalCountOfFoundBusinessOrganisations = 0;
  readonly pageSizeForBusinessOrganisationSearch = 100;

  readonly transportCompanyRelationTableColumns: TableColumn<TransportCompanyBoRelation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
      value: 'said',
    },
    { headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER', value: 'organisationNumber' },
    { headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION', value: 'abbreviation' },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      value: 'description',
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    {
      headerTitle: 'COMMON.VALID_TO',
      value: 'validTo',
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
    const selectedLanguage = this.translateService.currentLang ?? Language.DE;
    const abbreviationKey = `abbreviation${selectedLanguage[0].toUpperCase()}${
      selectedLanguage[1]
    }` as abbreviationType;
    return `${item.organisationNumber} (${item[abbreviationKey]})`;
  };

  ngOnInit() {
    this.transportCompany = this.dialogData.transportCompanyDetail[0];
    this.transportCompanyRelations = this.dialogData.transportCompanyDetail[1];
  }

  isAdmin(): boolean {
    return this.authService.hasRole(Role.BoAdmin);
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
      .pipe(switchMap(() => this.reloadRelations()))
      .subscribe();
  }

  deleteRelation(deleteEvent: {
    record: TransportCompanyBoRelation;
    callbackFn: () => void;
  }): void {
    this.transportCompanyRelationsService
      .deleteTransportCompanyRelation(deleteEvent.record.id!)
      .pipe(switchMap(() => this.reloadRelations()))
      .subscribe(() => deleteEvent.callbackFn());
  }

  private reloadRelations(): Observable<TransportCompanyBoRelation[]> {
    return this.transportCompanyRelationsService
      .getTransportCompanyRelations(
        this.transportCompany.id!,
        this.translateService.currentLang ?? Language.DE
      )
      .pipe(
        tap(
          (transportCompanyRelations) =>
            (this.transportCompanyRelations = transportCompanyRelations)
        )
      );
  }
}
