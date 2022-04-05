import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
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

@Component({
  selector: 'app-timetable-field-number-overview',
  templateUrl: './timetable-field-number-overview.component.html',
  styleUrls: ['./timetable-field-number-overview.component.scss'],
})
export class TimetableFieldNumberOverviewComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true })
  tableComponent!: TableComponent<TimetableFieldNumber>;

  tableColumns: TableColumn<TimetableFieldNumber>[] = [
    { headerTitle: 'TTFN.NUMBER', value: 'number' },
    { headerTitle: 'TTFN.DESCRIPTION', value: 'description' },
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'TTFN.BUSINESS_ORGANISATION', value: 'businessOrganisation' },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
  ];

  timetableFieldNumbers: TimetableFieldNumber[] = [];
  totalCount$ = 0;
  isLoading = false;
  private getVersionsSubscription!: Subscription;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private tableSettingsService: TableSettingsService,
    private routeToDialogService: RouteToDialogService
  ) {
    this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());
  }

  ngOnInit(): void {
    const storedTableSettings = this.tableSettingsService.getTableSettings(Pages.TTFN.path);
    this.getOverview(
      storedTableSettings || { page: 0, size: 10, sort: 'swissTimetableFieldNumber,ASC' }
    );
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(Pages.TTFN.path, $paginationAndSearch);
    this.isLoading = true;
    this.getVersionsSubscription = this.timetableFieldNumbersService
      .getOverview(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.validOn,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'ttfnid,ASC']
      )
      .pipe(
        catchError((err) => {
          this.notificationService.error(err, 'TTFN.NOTIFICATION.FETCH_ERROR');
          this.isLoading = false;
          throw err;
        })
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
  }
}
