import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  LineVersionSnapshot,
  WorkflowStatus,
} from '../../../../api';
import { TableColumn } from '../../../../core/components/table/table-column';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../../core/util/arrays';
import { TableFilterChip } from '../../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterMultiSelect } from '../../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterDateSelect } from '../../../../core/components/table-filter/config/table-filter-date-select';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../../pages';
import { LineInternalService } from '../../../../api/service/line-internal.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-lidi-workflow-overview',
  templateUrl: './lidi-workflow-overview.component.html',
  imports: [TableComponent, RouterOutlet, TranslatePipe],
})
export class LidiWorkflowOverviewComponent implements OnInit, OnDestroy {
  lineSnapshotsTableColumns: TableColumn<LineVersionSnapshot>[] = [
    { headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.NUMBER', value: 'number' },
    {
      headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.DESCRIPTION',
      value: 'description',
    },
    { headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.SLNID', value: 'slnid' },
    {
      headerTitle: 'LIDI.LINE_VERSION_SNAPSHOT.TABLE.STATUS',
      value: 'workflowStatus',
      translate: { withPrefix: 'WORKFLOW.STATUS.' },
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  private tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
    multiSelectWorkflowStatus: new TableFilterMultiSelect(
      'WORKFLOW.STATUS.',
      'COMMON.STATUS',
      [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected],
      1,
      'col-3',
      [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected]
    ),
    dateSelect: new TableFilterDateSelect(1, 'col-3'),
  };

  tableFilterConfig!: TableFilter<unknown>[][];

  lineVersionSnapshots: LineVersionSnapshot[] = [];
  totalCount$ = 0;

  private lineVersionSnapshotsSubscription?: Subscription;

  constructor(
    private lineInternalService: LineInternalService,
    private route: ActivatedRoute,
    private router: Router,
    private readonly tableService: TableService
  ) {
    const slnidFromQueryParam: string | undefined =
      this.route.snapshot.queryParams.slnid;
    if (slnidFromQueryParam) {
      this.tableFilterConfigIntern.chipSearch.addSearchFromString(
        slnidFromQueryParam
      );
    }
  }

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.WORKFLOWS
    );
  }

  getOverview(pagination: TablePagination) {
    this.lineVersionSnapshotsSubscription = this.lineInternalService
      .getLineVersionSnapshot(
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.dateSelect.getActiveSearch(),
        this.tableService.filter.multiSelectWorkflowStatus.getActiveSearch(),
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
  }
}
