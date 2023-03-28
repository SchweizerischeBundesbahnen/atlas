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
import {
  FilterType,
  TableFilterConfig,
  TableFilterDateSelect,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
} from '../../../core/components/table-filter/table-filter-config';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-lidi-lines',
  templateUrl: './lines.component.html',
  providers: [TableService],
})
export class LinesComponent implements OnDestroy {
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

  readonly tableFilterConfig: TableFilterConfig<LineType | Status | BusinessOrganisation>[] = [
    {
      filterType: FilterType.SEARCH_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: {} as BusinessOrganisation,
    },
    {
      filterType: FilterType.MULTI_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: [],
      labelTranslationKey: 'LIDI.TYPE',
      typeTranslationKeyPrefix: 'LIDI.LINE.TYPES.',
      selectOptions: Object.values(LineType),
    },
    {
      filterType: FilterType.MULTI_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: [],
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
  ];

  lineVersions: Line[] = [];
  totalCount$ = 0;

  private lineVersionsSubscription!: Subscription;
  private routeSubscription!: Subscription;

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
          // filterConfig: this.tableFilterConfig,
        })
      );
  }

  getOverview($paginationAndSearch: TablePagination) {
    this.lineVersionsSubscription = this.linesService
      .getLines(
        undefined,
        [],
        undefined,
        undefined,
        undefined,
        undefined,
        // ($paginationAndSearch.filterConfig[2] as TableFilterMultiSelect<Status>)?.activeSearch,
        // ($paginationAndSearch.filterConfig[1] as TableFilterMultiSelect<LineType>)?.activeSearch,
        // ($paginationAndSearch.filterConfig[0] as TableFilterSearchSelect<BusinessOrganisation>)
        //   ?.activeSearch?.sboid,
        // ($paginationAndSearch.filterConfig[3] as TableFilterDateSelect)?.activeSearch,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'slnid,asc']
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
    this.lineVersionsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
