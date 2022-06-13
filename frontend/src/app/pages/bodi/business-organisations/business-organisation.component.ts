import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { BusinessOrganisation, BusinessOrganisationsService } from '../../../api';
import { TableComponent } from '../../../core/components/table/table.component';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import { Pages } from '../../pages';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { Language } from '../../../core/components/language-switcher/language';

@Component({
  selector: 'app-bodi-lines',
  templateUrl: './business-organisation.component.html',
})
export class BusinessOrganisationComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true })
  tableComponent!: TableComponent<BusinessOrganisation>;

  tableColumns: TableColumn<BusinessOrganisation>[] = this.getColumns();

  businessOrganisations: BusinessOrganisation[] = [];
  totalCount$ = 0;
  isLoading = false;
  private businessOrganisationsSubscription!: Subscription;
  private routeSubscription!: Subscription;
  private langChangeSubscription!: Subscription;

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private tableSettingsService: TableSettingsService,
    private routeToDialogService: RouteToDialogService,
    public translateService: TranslateService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());

    this.langChangeSubscription = this.translateService.onLangChange.subscribe(
      () => (this.tableColumns = this.getColumns())
    );
  }

  ngOnInit(): void {
    const storedTableSettings = this.tableSettingsService.getTableSettings(
      Pages.BUSINESS_ORGANISATIONS.path
    );
    this.getOverview(storedTableSettings || { page: 0, size: 10, sort: this.getDefaultSort() });
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(
      Pages.BUSINESS_ORGANISATIONS.path,
      $paginationAndSearch
    );
    this.isLoading = true;
    this.businessOrganisationsSubscription = this.businessOrganisationsService
      .getAllBusinessOrganisations(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.validOn,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, this.getDefaultSort()]
      )
      .subscribe((container) => {
        this.businessOrganisations = container.objects!;
        this.totalCount$ = container.totalCount!;
        this.tableComponent.setTableSettings($paginationAndSearch);
        this.isLoading = false;
      });
  }

  editVersion($event: BusinessOrganisation) {
    this.router
      .navigate([$event.sboid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.businessOrganisationsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
    this.langChangeSubscription.unsubscribe();
  }

  getDefaultSort() {
    return this.displayedDescription() + ',ASC';
  }

  displayedDescription() {
    return ('description' + this.formatedLanguage()) as
      | 'descriptionDe'
      | 'descriptionFr'
      | 'descriptionIt';
  }

  displayedAbbreviation() {
    return ('abbreviation' + this.formatedLanguage()) as
      | 'abbreviationDe'
      | 'abbreviationFr'
      | 'abbreviationIt';
  }

  private formatedLanguage() {
    const currentLanguage = this.translateService.currentLang || Language.DE;
    return currentLanguage.charAt(0).toUpperCase() + currentLanguage.slice(1);
  }

  private getColumns(): TableColumn<BusinessOrganisation>[] {
    return [
      { headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION', value: this.displayedDescription() },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
        value: this.displayedAbbreviation(),
      },
      { headerTitle: 'BODI.BUSINESS_ORGANISATION.SBOID', value: 'sboid' },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
        value: 'organisationNumber',
      },
      { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
      { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    ];
  }
}
