import { Component, OnDestroy, OnInit } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TableColumn } from '../../../core/components/table/table-column';
import { TimetableFieldNumbersService, Version } from '../../../api/ttfn';
import { NotificationService } from '../../../core/notification/notification.service';
import { TablePagination } from '../../../core/components/table/table-pagination';

@Component({
  selector: 'app-timetable-field-number-overview',
  templateUrl: './timetable-field-number-overview.component.html',
  styleUrls: ['./timetable-field-number-overview.component.scss'],
})
export class TimetableFieldNumberOverviewComponent implements OnInit, OnDestroy {
  tableColumns: TableColumn<Version>[] = [
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TTFN.NAME', value: 'name' },
    { headerTitle: 'TTFN.STATUS', value: 'status' },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  versions$: Version[] = [];
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
    this.getVersions({ page: 0, size: 10, sort: 'swissTimetableFieldNumber,ASC' });
  }

  getVersions($pagination: TablePagination) {
    this.isLoading = true;
    this.getVersionsSubscription = this.timetableFieldNumbersService
      .getVersions($pagination.page, $pagination.size, [$pagination.sort!])
      .pipe(
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.FETCH_ERROR');
          this.isLoading = false;
          throw err;
        })
      )
      .subscribe((versionContainer) => {
        this.versions$ = versionContainer.versions!;
        this.totalCount$ = versionContainer.totalCount!;
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

  editVersion($event: Version) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.getVersionsSubscription.unsubscribe();
  }
}
