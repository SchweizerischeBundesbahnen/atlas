import { Component, OnDestroy, OnInit } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { NotificationService } from '../../../core/notification/notification.service';
import { LinesService, LineVersion } from '../../../api/lidi';

@Component({
  selector: 'app-lidi-lines',
  templateUrl: './lines.component.html',
  styleUrls: ['./lines.component.scss'],
})
export class LinesComponent implements OnInit, OnDestroy {
  linesTableColumns: TableColumn<LineVersion>[] = [
    { headerTitle: 'LIDI.SWISS_LINE_NUMBER', value: 'swissLineNumber' },
    { headerTitle: 'LIDI.SHORT_NAME', value: 'shortName' },
    { headerTitle: 'LIDI.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.STATUS', value: 'status' },
    { headerTitle: 'LIDI.TYPE', value: 'type' },
    { headerTitle: 'LIDI.BUSINESS_ORGANISATION', value: 'businessOrganisation' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    { headerTitle: 'LIDI.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'LIDI.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  lineVersions: LineVersion[] = [];
  totalCount$ = 0;
  isLoading = false;
  private lineVersionsSubscription!: Subscription;

  constructor(
    private linesService: LinesService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.getVersions({ page: 0, size: 10, sort: 'swissLineNumber,ASC' });
  }

  getVersions($pagination: TablePagination) {
    this.isLoading = true;
    this.lineVersionsSubscription = this.linesService
      .getLineVersions($pagination.page, $pagination.size, [$pagination.sort!])
      .pipe(
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.FETCH_ERROR');
          this.isLoading = false;
          throw err;
        })
      )
      .subscribe((lineVersionContainer) => {
        this.lineVersions = lineVersionContainer.versions!;
        this.totalCount$ = lineVersionContainer.totalCount!;
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

  editVersion($event: LineVersion) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.lineVersionsSubscription.unsubscribe();
  }
}
