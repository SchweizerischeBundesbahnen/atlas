import { Component, OnDestroy } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { BusinessOrganisation, Line, LinesService, LineType, Status } from '../../../api';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { FormControl, FormGroup } from '@angular/forms';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterSearchSelect } from '../../../core/components/table-filter/config/table-filter-search-select';
import { TableFilterSearchType } from '../../../core/components/table-filter/config/table-filter-search-type';
import { TableFilterMultiSelect } from '../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { TableFilterDateSelect } from '../../../core/components/table-filter/config/table-filter-date-select';

@Component({
  selector: 'app-lidi-lines',
  templateUrl: './lines.component.html',
  providers: [TableService],
})
export class LinesComponent implements OnDestroy {
  private readonly tableFilterConfigIntern = {
    chipSearch: new TableFilterChip('col-6'),
    searchSelect: new TableFilterSearchSelect<BusinessOrganisation>(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      'col-3',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    ),
    multiSelectLineType: new TableFilterMultiSelect(
      'LIDI.LINE.TYPES.',
      'LIDI.TYPE',
      Object.values(LineType),
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

  private lineVersionsSubscription?: Subscription;
  private routeSubscription: Subscription;

  linesTableColumns: TableColumn<Line>[] = [
    { headerTitle: 'LIDI.LINE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.LINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SWISS_LINE_NUMBER', value: 'swissLineNumber' },
    { headerTitle: 'LIDI.TYPE', value: 'lineType', translate: { withPrefix: 'LIDI.LINE.TYPES.' } },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  tableFilterConfig: TableFilter<unknown>[][] = [
    [this.tableFilterConfigIntern.chipSearch],
    [
      this.tableFilterConfigIntern.searchSelect,
      this.tableFilterConfigIntern.multiSelectLineType,
      this.tableFilterConfigIntern.multiSelectStatus,
      this.tableFilterConfigIntern.dateSelect,
    ],
  ];

  lineVersions: Line[] = [];
  totalCount$ = 0;

  constructor(
    private linesService: LinesService,
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
    this.lineVersionsSubscription = this.linesService
      .getLines(
        undefined,
        this.tableFilterConfigIntern.chipSearch.getActiveSearch(),
        this.tableFilterConfigIntern.multiSelectStatus.getActiveSearch(),
        this.tableFilterConfigIntern.multiSelectLineType.getActiveSearch(),
        this.tableFilterConfigIntern.searchSelect.getActiveSearch()?.sboid,
        this.tableFilterConfigIntern.dateSelect.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'slnid,asc')
      )
      .subscribe((lineContainer) => {
        this.lineVersions = lineContainer.objects!;
        this.totalCount$ = lineContainer.totalCount!;
      });
  }

  editVersion($event: Line) {
    this.router
      .navigate([$event.slnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.lineVersionsSubscription?.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
