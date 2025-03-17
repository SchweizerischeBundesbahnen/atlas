import { Component, OnDestroy, OnInit } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { BusinessOrganisation, BusinessOrganisationsService, Status } from '../../../api';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterMultiSelect } from '../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterDateSelect } from '../../../core/components/table-filter/config/table-filter-date-select';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../pages';

@Component({
    selector: 'app-bodi-business-organisations',
    templateUrl: './business-organisation.component.html',
    standalone: false
})
export class BusinessOrganisationComponent implements OnInit, OnDestroy {
  tableColumns: TableColumn<BusinessOrganisation>[] = this.getColumns();

  private tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
    multiSelectStatus: new TableFilterMultiSelect(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      1,
      'col-3',
      DEFAULT_STATUS_SELECTION
    ),
    dateSelect: new TableFilterDateSelect(1, 'col-3'),
  };

  tableFilterConfig!: TableFilter<unknown>[][];

  businessOrganisations: BusinessOrganisation[] = [];
  totalCount$ = 0;

  private businessOrganisationsSubscription?: Subscription;
  private langChangeSubscription: Subscription;

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private route: ActivatedRoute,
    private router: Router,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
    private tableService: TableService
  ) {
    this.langChangeSubscription = this.businessOrganisationLanguageService
      .languageChanged()
      .subscribe(() => (this.tableColumns = this.getColumns()));
  }

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.BUSINESS_ORGANISATIONS
    );
  }

  getOverview(pagination: TablePagination) {
    this.businessOrganisationsSubscription = this.businessOrganisationsService
      .getAllBusinessOrganisations(
        this.tableService.filter.chipSearch.getActiveSearch(),
        undefined,
        this.tableService.filter.dateSelect.getActiveSearch(),
        this.tableService.filter.multiSelectStatus.getActiveSearch(),
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
