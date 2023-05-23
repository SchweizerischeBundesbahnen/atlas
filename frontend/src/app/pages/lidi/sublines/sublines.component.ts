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
import { FormControl, FormGroup } from '@angular/forms';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import {
  TableFilterChip,
  TableFilterConfig,
  TableFilterDateSelect,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
  TableFilterSearchType,
} from '../../../core/components/table-filter/table-filter-config';

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

  private readonly tableFilterConfigIntern = {
    chipSearch: new TableFilterChip('col-6'),
    searchSelect: new TableFilterSearchSelect<BusinessOrganisation>(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      'col-3',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    ),
    multiSelectSublineType: new TableFilterMultiSelect(
      'LIDI.SUBLINE.TYPES.',
      'LIDI.SUBLINE_TYPE',
      Object.values(SublineType),
      'col-3'
    ),
    multiSelectStatus: new TableFilterMultiSelect(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      'col-3',
      [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn]
    ),
    dateSelect: new TableFilterDateSelect('col-3'),
  };

  tableFilterConfig: TableFilterConfig<unknown>[][] = [
    [this.tableFilterConfigIntern.chipSearch],
    [
      this.tableFilterConfigIntern.searchSelect,
      this.tableFilterConfigIntern.multiSelectSublineType,
      this.tableFilterConfigIntern.multiSelectStatus,
      this.tableFilterConfigIntern.dateSelect,
    ],
  ];

  sublines: Subline[] = [];
  totalCount$ = 0;

  private sublineVersionsSubscription?: Subscription;
  private routeSubscription: Subscription;

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

  getOverview(pagination: TablePagination) {
    this.sublineVersionsSubscription = this.sublinesService
      .getSublines(
        this.tableFilterConfigIntern.chipSearch.getActiveSearch(),
        this.tableFilterConfigIntern.multiSelectStatus.getActiveSearch(),
        this.tableFilterConfigIntern.multiSelectSublineType.getActiveSearch(),
        this.tableFilterConfigIntern.searchSelect.getActiveSearch()?.sboid,
        this.tableFilterConfigIntern.dateSelect.getActiveSearch(),
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
    this.routeSubscription.unsubscribe();
  }
}
