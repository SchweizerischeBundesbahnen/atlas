import { Component, OnDestroy } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { BusinessOrganisation, Status, Subline, SublinesService, SublineType } from '../../../api';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { filter } from 'rxjs/operators';
import {
  FilterType,
  getActiveSearch,
  getActiveSearchDate,
  getActiveSearchForChip,
  TableFilterChip,
  TableFilterDateSelect,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
} from '../../../core/components/table-filter/table-filter-config';
import { FormControl } from '@angular/forms';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';

@Component({
  selector: 'app-lidi-sublines',
  templateUrl: './sublines.component.html',
  providers: [TableService],
})
export class SublinesComponent implements OnDestroy {
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

  readonly tableFilterConfig: [
    [TableFilterChip],
    [
      TableFilterSearchSelect<BusinessOrganisation>,
      TableFilterMultiSelect<SublineType>,
      TableFilterMultiSelect<Status>,
      TableFilterDateSelect
    ]
  ] = [
    [
      {
        filterType: FilterType.CHIP_SEARCH,
        elementWidthCssClass: 'col-6',
        activeSearch: [],
      },
    ],
    [
      {
        filterType: FilterType.SEARCH_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: {} as BusinessOrganisation,
      },
      {
        filterType: FilterType.MULTI_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: [],
        labelTranslationKey: 'LIDI.SUBLINE_TYPE',
        typeTranslationKeyPrefix: 'LIDI.SUBLINE.TYPES.',
        selectOptions: Object.values(SublineType),
      },
      {
        filterType: FilterType.MULTI_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn],
        labelTranslationKey: 'COMMON.STATUS',
        typeTranslationKeyPrefix: 'COMMON.STATUS_TYPES.',
        selectOptions: Object.values(Status),
      },
      {
        filterType: FilterType.VALID_ON_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: undefined,
        formControl: new FormControl(),
      },
    ],
  ];

  sublines: Subline[] = [];
  totalCount$ = 0;

  private sublineVersionsSubscription!: Subscription;
  private routeSubscription!: Subscription;

  constructor(
    private sublinesService: SublinesService,
    private route: ActivatedRoute,
    private router: Router,
    private routeToDialogService: RouteToDialogService,
    private readonly tableService: TableService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() =>
        this.getOverview({
          page: this.tableService.pageIndex,
          size: this.tableService.pageSize,
          sort: this.tableService.sortString,
        })
      );
  }

  getOverview($paginationAndSearch: TablePagination) {
    this.sublineVersionsSubscription = this.sublinesService
      .getSublines(
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        getActiveSearch(this.tableFilterConfig[1][2]),
        getActiveSearch(this.tableFilterConfig[1][1]),
        getActiveSearch<BusinessOrganisation, BusinessOrganisation>(this.tableFilterConfig[1][0])
          .sboid,
        getActiveSearchDate(this.tableFilterConfig[1][3]),
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'slnid,asc']
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
    this.sublineVersionsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
