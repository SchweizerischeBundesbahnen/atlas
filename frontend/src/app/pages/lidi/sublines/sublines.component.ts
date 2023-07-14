import { Component, OnDestroy, OnInit } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { BusinessOrganisation, Status, Subline, SublinesService, SublineType } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterSearchSelect } from '../../../core/components/table-filter/config/table-filter-search-select';
import { TableFilterSearchType } from '../../../core/components/table-filter/config/table-filter-search-type';
import { TableFilterMultiSelect } from '../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { TableFilterDateSelect } from '../../../core/components/table-filter/config/table-filter-date-select';
import { Pages } from '../../pages';

@Component({
  selector: 'app-lidi-sublines',
  templateUrl: './sublines.component.html',
})
export class SublinesComponent implements OnInit, OnDestroy {
  private readonly tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
    searchSelect: new TableFilterSearchSelect<BusinessOrganisation>(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      1,
      'col-3',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    ),
    multiSelectSublineType: new TableFilterMultiSelect(
      'LIDI.SUBLINE.TYPES.',
      'LIDI.SUBLINE_TYPE',
      Object.values(SublineType),
      1,
      'col-3'
    ),
    multiSelectStatus: new TableFilterMultiSelect(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      1,
      'col-3',
      [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn]
    ),
    dateSelect: new TableFilterDateSelect(1, 'col-3'),
  };

  private sublineVersionsSubscription?: Subscription;

  sublinesTableColumns: TableColumn<Subline>[] = [
    { headerTitle: 'LIDI.SUBLINE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.SUBLINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SWISS_SUBLINE_NUMBER', value: 'swissSublineNumber' },
    { headerTitle: 'LIDI.SUBLINE.OVERVIEW_MAINLINE', value: 'swissLineNumber' },
    {
      headerTitle: 'LIDI.SUBLINE_TYPE',
      value: 'sublineType',
      translate: { withPrefix: 'LIDI.SUBLINE.TYPES.' },
    },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  tableFilterConfig!: TableFilter<unknown>[][];

  sublines: Subline[] = [];
  totalCount$ = 0;

  constructor(
    private sublinesService: SublinesService,
    private route: ActivatedRoute,
    private router: Router,
    private tableService: TableService
  ) {}

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.SUBLINES
    );
  }

  getOverview(pagination: TablePagination) {
    this.sublineVersionsSubscription = this.sublinesService
      .getSublines(
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatus.getActiveSearch(),
        this.tableService.filter.multiSelectSublineType.getActiveSearch(),
        this.tableService.filter.searchSelect.getActiveSearch()?.sboid,
        this.tableService.filter.dateSelect.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'slnid,asc')
      )
      .subscribe((sublineContainer) => {
        this.sublines = sublineContainer.objects!;
        this.totalCount$ = sublineContainer.totalCount!;
      });
  }

  editVersion($event: Subline) {
    this.router
      .navigate([$event.slnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.sublineVersionsSubscription?.unsubscribe();
  }
}
