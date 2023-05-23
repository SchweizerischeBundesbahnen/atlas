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
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../../core/util/arrays';
import {
  TableFilterChip,
  TableFilterConfig,
  TableFilterDateSelect,
  TableFilterMultiSelect,
} from '../../../../core/components/table-filter/table-filter-config';

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

  private readonly tableFilterConfigIntern = {
    chipSearch: new TableFilterChip('col-6'),
    multiSelectWorkflowStatus: new TableFilterMultiSelect(
      'WORKFLOW.STATUS.',
      'COMMON.STATUS',
      [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected],
      'col-3',
      [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected]
    ),
    dateSelect: new TableFilterDateSelect('col-3'),
  };

  tableFilterConfig: TableFilterConfig<unknown>[][] = [
    [this.tableFilterConfigIntern.chipSearch],
    [
      this.tableFilterConfigIntern.multiSelectWorkflowStatus,
      this.tableFilterConfigIntern.dateSelect,
    ],
  ];

  lineVersionSnapshots: LineVersionSnapshot[] = [];
  totalCount$ = 0;

  private lineVersionSnapshotsSubscription?: Subscription;
  private routeSubscription: Subscription;

  constructor(
    private linesService: LinesService,
    private route: ActivatedRoute,
    private router: Router,
    private routeToDialogService: RouteToDialogService,
    private readonly tableService: TableService
  ) {
    const slnidFromQueryParam: string | undefined = this.route.snapshot.queryParams.slnid;
    if (slnidFromQueryParam) {
      this.tableFilterConfigIntern.chipSearch.addSearchFromString(slnidFromQueryParam);
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

  getOverview(pagination: TablePagination) {
    this.lineVersionSnapshotsSubscription = this.linesService
      .getLineVersionSnapshot(
        this.tableFilterConfigIntern.chipSearch.getActiveSearch(),
        this.tableFilterConfigIntern.dateSelect.getActiveSearch(),
        this.tableFilterConfigIntern.multiSelectWorkflowStatus.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'number,asc')
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
    this.lineVersionSnapshotsSubscription?.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
