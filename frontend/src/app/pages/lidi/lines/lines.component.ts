import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { Line, LinesService, LineType } from '../../../api';
import { TableComponent } from '../../../core/components/table/table.component';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import { Pages } from '../../pages';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';

@Component({
  selector: 'app-lidi-lines',
  templateUrl: './lines.component.html',
})
export class LinesComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true }) tableComponent!: TableComponent<Line>;

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

  readonly LINE_TYPES: LineType[] = Object.values(LineType);
  activeLineTypes: LineType[] = [];
  lineVersions: Line[] = [];
  totalCount$ = 0;
  isLoading = false;
  private lineVersionsSubscription!: Subscription;
  private routeSubscription!: Subscription;

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
    const storedTableSettings = this.tableSettingsService.getTableSettings(Pages.LINES.path);
    this.getOverview(
      storedTableSettings || {
        page: 0,
        size: 10,
        sort: 'number,ASC',
        statusChoices: DEFAULT_STATUS_SELECTION,
      }
    );
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(Pages.LINES.path, $paginationAndSearch);
    this.isLoading = true;
    this.lineVersionsSubscription = this.linesService
      .getLines(
        undefined,
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.lineTypes,
        $paginationAndSearch.boChoice,
        $paginationAndSearch.validOn,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'slnid,ASC']
      )
      .subscribe((lineContainer) => {
        this.lineVersions = lineContainer.objects!;
        this.totalCount$ = lineContainer.totalCount!;
        this.tableComponent.setTableSettings($paginationAndSearch);
        this.activeLineTypes = $paginationAndSearch.lineTypes;
        this.isLoading = false;
      });
  }

  onLineTypeSelectionChange(): void {
    this.tableComponent.searchData({
      ...this.tableComponent.tableSearchComponent.activeSearch,
      lineTypes: this.activeLineTypes,
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
