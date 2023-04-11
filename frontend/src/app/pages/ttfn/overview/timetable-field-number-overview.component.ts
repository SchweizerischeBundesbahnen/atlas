import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TableColumn } from '../../../core/components/table/table-column';
import {
  BusinessOrganisation,
  Status,
  TimetableFieldNumber,
  TimetableFieldNumbersService,
} from '../../../api';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { filter } from 'rxjs/operators';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
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
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';

@Component({
  selector: 'app-timetable-field-number-overview',
  templateUrl: './timetable-field-number-overview.component.html',
  providers: [TableService],
})
export class TimetableFieldNumberOverviewComponent implements OnDestroy {
  tableColumns: TableColumn<TimetableFieldNumber>[] = [
    { headerTitle: 'TTFN.NUMBER', value: 'number' },
    { headerTitle: 'TTFN.DESCRIPTION', value: 'description' },
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
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
        activeSearch: DEFAULT_STATUS_SELECTION,
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

  timetableFieldNumbers: TimetableFieldNumber[] = [];
  totalCount$ = 0;

  private getVersionsSubscription?: Subscription;
  private routeSubscription: Subscription;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private route: ActivatedRoute,
    private router: Router,
    private routeToDialogService: RouteToDialogService,
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
  }

  getOverview(pagination: TablePagination) {
    this.getVersionsSubscription = this.timetableFieldNumbersService
      .getOverview(
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        undefined,
        getActiveSearch<BusinessOrganisation | undefined, BusinessOrganisation>(
          this.tableFilterConfig[1][0]
        )?.sboid,
        getActiveSearchDate(this.tableFilterConfig[1][2]),
        getActiveSearch(this.tableFilterConfig[1][1]),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'ttfnid,asc')
      )
      .subscribe((container) => {
        this.timetableFieldNumbers = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  newVersion() {
    this.router
      .navigate(['add'], {
        relativeTo: this.route,
      })
      .then();
  }

  editVersion($event: TimetableFieldNumber) {
    this.router
      .navigate([$event.ttfnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.getVersionsSubscription?.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
