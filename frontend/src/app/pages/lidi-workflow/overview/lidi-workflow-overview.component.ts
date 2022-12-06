import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { TableComponent } from '../../../core/components/table/table.component';
import { LinesService, LineVersionSnapshot } from '../../../api';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationService } from '../../../core/notification/notification.service';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { TableSettingsWorkflow } from '../../../core/components/table/table-settings-workflow';
import { DEFAULT_WORKFLOW_STATUS_SELECTION } from '../../../core/constants/workflow-status.choices';

@Component({
  selector: 'app-lidi-workflow-overview',
  templateUrl: './lidi-workflow-overview.component.html',
})
export class LidiWorkflowOverviewComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true }) tableComponent!: TableComponent<LineVersionSnapshot>;

  lineSnapshotsTableColumns: TableColumn<LineVersionSnapshot>[] = [
    { headerTitle: 'LIDI.LINE_WORKFLOW.TABLE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.LINE_WORKFLOW.TABLE.DESCRIPTION', value: 'description' },
    {
      headerTitle: 'LIDI.LINE_WORKFLOW.TABLE.STATUS',
      value: 'workflowStatus',
      translate: { withPrefix: 'WORKFLOW.STATUS.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  lineVersionSnapshots: LineVersionSnapshot[] = [];
  totalCount$ = 0;
  isLoading = false;
  private routeSubscription!: Subscription;
  private lineVersionSnapshotsSubscription!: Subscription;

  constructor(
    private linesService: LinesService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private tableSettingsService: TableSettingsService,
    private routeToDialogService: RouteToDialogService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());
  }

  ngOnInit(): void {
    this.getOverview({
      page: 0,
      size: 10,
      sort: 'number,ASC',
      statusChoices: DEFAULT_WORKFLOW_STATUS_SELECTION,
    });
  }

  getOverview($paginationAndSearch: TableSettingsWorkflow) {
    this.isLoading = true;
    this.lineVersionSnapshotsSubscription = this.linesService
      .getLineVersionSnapshotModels(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.validOn,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'number,ASC']
      )
      .subscribe((lineVersionSnapshotContainer) => {
        this.lineVersionSnapshots = lineVersionSnapshotContainer.objects!;
        this.totalCount$ = lineVersionSnapshotContainer.totalCount!;
        this.tableComponent.setTableSettings($paginationAndSearch);
        this.isLoading = false;
      });
  }

  editVersion($event: LineVersionSnapshot) {
    this.router
      .navigate([$event.slnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.lineVersionSnapshotsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
