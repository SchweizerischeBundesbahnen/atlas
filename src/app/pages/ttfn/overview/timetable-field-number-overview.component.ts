import { Component, OnDestroy, OnInit } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TableColumn } from '../../../core/components/table/table-column';
import { TimetableFieldNumber, TimetableFieldNumbersService } from '../../../api';
import { NotificationService } from '../../../core/notification/notification.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { TableSearch } from '../../../core/components/table-search/table-search';

@Component({
  selector: 'app-timetable-field-number-overview',
  templateUrl: './timetable-field-number-overview.component.html',
  styleUrls: ['./timetable-field-number-overview.component.scss'],
})
export class TimetableFieldNumberOverviewComponent implements OnInit, OnDestroy {
  tableColumns: TableColumn<TimetableFieldNumber>[] = [
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TTFN.NAME', value: 'name' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  timetableFieldNumbers: TimetableFieldNumber[] = [];
  totalCount$ = 0;
  isLoading = false;
  private getVersionsSubscription!: Subscription;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.getOverview({ page: 0, size: 10, sort: 'swissTimetableFieldNumber,ASC' });
  }

  getOverview($paginationAndSearch: TablePagination & TableSearch) {
    this.isLoading = true;
    this.getVersionsSubscription = this.timetableFieldNumbersService
      .getOverview(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.validOn,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!]
      )
      .pipe(
        catchError((err) => {
          this.notificationService.error(err);
          this.isLoading = false;
          throw err;
        })
      )
      .subscribe((container) => {
        this.timetableFieldNumbers = container.fieldNumbers!;
        this.totalCount$ = container.totalCount!;
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
