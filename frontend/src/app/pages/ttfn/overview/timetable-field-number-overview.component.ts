import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TableColumn } from '../../../core/components/table/table-column';
import { TimetableFieldNumber, TimetableFieldNumbersService } from '../../../api';
import { NotificationService } from '../../../core/notification/notification.service';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import { Pages } from '../../pages';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { filter } from 'rxjs/operators';
import { TableComponent } from '../../../core/components/table/table.component';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';

@Component({
  selector: 'app-timetable-field-number-overview',
  templateUrl: './timetable-field-number-overview.component.html',
})
export class TimetableFieldNumberOverviewComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true })
  tableComponent!: TableComponent<TimetableFieldNumber>;

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

  timetableFieldNumbers: TimetableFieldNumber[] = [];
  totalCount$ = 0;
  isLoading = false;
  private getVersionsSubscription!: Subscription;
  private routeSubscription!: Subscription;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
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
    const storedTableSettings = this.tableSettingsService.getTableSettings(Pages.TTFN.path);
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
    this.tableSettingsService.storeTableSettings(Pages.TTFN.path, $paginationAndSearch);
    this.isLoading = true;
    this.getVersionsSubscription = this.timetableFieldNumbersService
      .getOverview(
        $paginationAndSearch.searchCriteria,
        undefined,
        $paginationAndSearch.boChoice,
        $paginationAndSearch.validOn,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'ttfnid,ASC']
      )
      .subscribe((container) => {
        this.timetableFieldNumbers = container.objects!;
        this.totalCount$ = container.totalCount!;
        this.tableComponent.setTableSettings($paginationAndSearch);
        this.isLoading = false;
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
    this.getVersionsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
