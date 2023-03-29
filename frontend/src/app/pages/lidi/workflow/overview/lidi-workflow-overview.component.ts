import { Component, OnDestroy } from '@angular/core';
import { LinesService, LineVersionSnapshot, WorkflowStatus } from '../../../../api';
import { TableColumn } from '../../../../core/components/table/table-column';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../../core/components/route-to-dialog/route-to-dialog.service';
import { filter } from 'rxjs/operators';
import { TableService } from '../../../../core/components/table/table.service';
import {
  FilterType,
  getActiveSearch,
  getActiveSearchDate,
  getActiveSearchForChip,
  TableFilterChip,
  TableFilterDateSelect,
  TableFilterMultiSelect,
} from '../../../../core/components/table-filter/table-filter-config';
import { FormControl } from '@angular/forms';
import { TablePagination } from '../../../../core/components/table/table-pagination';

@Component({
  selector: 'app-lidi-workflow-overview',
  templateUrl: './lidi-workflow-overview.component.html',
  providers: [TableService],
})
export class LidiWorkflowOverviewComponent implements OnDestroy {
  lineSnapshotsTableColumns: TableColumn<LineVersionSnapshot>[] = [
    { headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.SLNID', value: 'slnid' },
    {
      headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.STATUS',
      value: 'workflowStatus',
      translate: { withPrefix: 'WORKFLOW.STATUS.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  readonly tableFilterConfig: [
    [TableFilterChip],
    [TableFilterMultiSelect<WorkflowStatus>, TableFilterDateSelect]
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
        filterType: FilterType.MULTI_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected],
        labelTranslationKey: 'COMMON.STATUS',
        typeTranslationKeyPrefix: 'WORKFLOW.STATUS.',
        selectOptions: [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected],
      },
      {
        filterType: FilterType.VALID_ON_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: undefined,
        formControl: new FormControl(),
      },
    ],
  ];

  lineVersionSnapshots: LineVersionSnapshot[] = [];
  totalCount$ = 0;
  private routeSubscription!: Subscription;
  private lineVersionSnapshotsSubscription!: Subscription;

  constructor(
    private linesService: LinesService,
    private route: ActivatedRoute,
    private router: Router,
    private routeToDialogService: RouteToDialogService,
    private readonly tableService: TableService
  ) {
    const slnidFromQueryParam: string | undefined = this.route.snapshot.queryParams.slnid;
    if (slnidFromQueryParam) {
      this.tableFilterConfig[0][0].activeSearch.push(slnidFromQueryParam);
    }

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

  getOverview($paginationAndSearch: TablePagination) {
    this.lineVersionSnapshotsSubscription = this.linesService
      .getLineVersionSnapshot(
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        getActiveSearchDate(this.tableFilterConfig[1][1]),
        getActiveSearch(this.tableFilterConfig[1][0]),
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'number,asc']
      )
      .subscribe((lineVersionSnapshotContainer) => {
        this.lineVersionSnapshots = lineVersionSnapshotContainer.objects!;
        this.totalCount$ = lineVersionSnapshotContainer.totalCount!;
      });
  }

  navigateToDetails($event: LineVersionSnapshot) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.lineVersionSnapshotsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
