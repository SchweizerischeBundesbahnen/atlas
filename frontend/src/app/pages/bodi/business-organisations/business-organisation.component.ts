import { Component, OnDestroy } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { BusinessOrganisation, BusinessOrganisationsService, Status } from '../../../api';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import {
  TableFilterChipClass,
  TableFilterConfigClass,
  TableFilterDateSelectClass,
  TableFilterMultiSelectClass,
} from '../../../core/components/table-filter/table-filter-config-class';

@Component({
  selector: 'app-bodi-business-organisations',
  templateUrl: './business-organisation.component.html',
  providers: [TableService],
})
export class BusinessOrganisationComponent implements OnDestroy {
  tableColumns: TableColumn<BusinessOrganisation>[] = this.getColumns();

  private readonly tableFilterConfigIntern = {
    chipSearch: new TableFilterChipClass('col-6'),
    multiSelectStatus: new TableFilterMultiSelectClass(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      'col-3',
      DEFAULT_STATUS_SELECTION
    ),
    dateSelect: new TableFilterDateSelectClass('col-3'),
  };

  readonly tableFilterConfig: TableFilterConfigClass<unknown>[][] = [
    [this.tableFilterConfigIntern.chipSearch],
    [this.tableFilterConfigIntern.multiSelectStatus, this.tableFilterConfigIntern.dateSelect],
  ];

  businessOrganisations: BusinessOrganisation[] = [];
  totalCount$ = 0;

  private businessOrganisationsSubscription?: Subscription;
  private routeSubscription: Subscription;
  private langChangeSubscription: Subscription;

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private route: ActivatedRoute,
    private router: Router,
    private routeToDialogService: RouteToDialogService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
    private readonly tableService: TableService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => {
        this.getOverview({
          page: this.tableService.pageIndex,
          size: this.tableService.pageSize,
          sort: this.tableService.sortString,
        });
      });

    this.langChangeSubscription = this.businessOrganisationLanguageService
      .languageChanged()
      .subscribe(() => (this.tableColumns = this.getColumns()));
  }

  getOverview(pagination: TablePagination) {
    this.businessOrganisationsSubscription = this.businessOrganisationsService
      .getAllBusinessOrganisations(
        this.tableFilterConfigIntern.chipSearch.getActiveSearch(),
        undefined,
        this.tableFilterConfigIntern.dateSelect.getActiveSearch(),
        this.tableFilterConfigIntern.multiSelectStatus.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, this.getDefaultSort())
      )
      .subscribe((container) => {
        this.businessOrganisations = container.objects!;
        this.totalCount$ = container.totalCount!;
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
    this.businessOrganisationsSubscription?.unsubscribe();
    this.routeSubscription.unsubscribe();
    this.langChangeSubscription.unsubscribe();
  }

  getDefaultSort() {
    return this.getCurrentLanguageDescription() + ',asc';
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
