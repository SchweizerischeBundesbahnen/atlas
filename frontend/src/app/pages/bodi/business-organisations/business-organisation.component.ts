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
import { BusinessOrganisationLanguageService } from '../../../core/service/business-organisation-language.service';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';

@Component({
  selector: 'app-bodi-business-organisations',
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
    public businessOrganisationLanguageService: BusinessOrganisationLanguageService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());

    this.langChangeSubscription = this.businessOrganisationLanguageService
      .languageChanged()
      .subscribe(() => (this.tableColumns = this.getColumns()));
  }

  ngOnInit(): void {
    const storedTableSettings = this.tableSettingsService.getTableSettings(
      Pages.BUSINESS_ORGANISATIONS.path
    );
    this.getOverview(
      storedTableSettings || {
        page: 0,
        size: 10,
        sort: this.getDefaultSort(),
        statusChoices: DEFAULT_STATUS_SELECTION,
      }
    );
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
        undefined,
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
    return this.getCurrentLanguageDescription() + ',ASC';
  }

  private getCurrentLanguageAbbreviation() {
    return this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation();
  }

  private getCurrentLanguageDescription() {
    return this.businessOrganisationLanguageService.getCurrentLanguageDescription();
  }

  private getColumns(): TableColumn<BusinessOrganisation>[] {
    return [
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
        value: this.getCurrentLanguageDescription(),
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
        value: this.getCurrentLanguageAbbreviation(),
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
