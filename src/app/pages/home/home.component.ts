import { Component, OnDestroy, OnInit } from '@angular/core';
import { TimetableFieldNumbersService, Version } from '../../api';

import { TableColumn } from '../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TablePagination } from '../../core/components/table/table-pagination';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  tableColumns: TableColumn<Version>[] = [
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TTFN.NAME', value: 'name' },
    { headerTitle: 'TTFN.STATUS', value: 'status' },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
    { headerTitle: 'TTFN.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'TTFN.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  versions$: Version[] = [];
  totalCount$ = 0;
  isLoading = false;
  private getVersionsSubscription!: Subscription;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private route: ActivatedRoute,
    private router: Router
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
          // implement Notification Service and integrate me the snack-bar notification
          // see ATLAS-116
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
