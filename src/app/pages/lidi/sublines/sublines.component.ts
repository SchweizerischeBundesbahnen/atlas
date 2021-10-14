import { Component, OnDestroy, OnInit } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { NotificationService } from '../../../core/notification/notification.service';
import { SublinesService, SublineVersion } from '../../../api/lidi';

@Component({
  selector: 'app-lidi-sublines',
  templateUrl: './sublines.component.html',
  styleUrls: ['./sublines.component.scss'],
})
export class SublinesComponent implements OnInit, OnDestroy {
  sublinesTableColumns: TableColumn<SublineVersion>[] = [
    { headerTitle: 'LIDI.SWISS_SUBLINE_NUMBER', value: 'swissSublineNumber' },
    { headerTitle: 'LIDI.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SWISS_LINE_NUMBER', value: 'swissLineNumber' },
    { headerTitle: 'LIDI.STATUS', value: 'status' },
    { headerTitle: 'LIDI.SUBLINE_TYPE', value: 'type' },
    { headerTitle: 'LIDI.BUSINESS_ORGANISATION', value: 'businessOrganisation' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    { headerTitle: 'LIDI.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'LIDI.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  sublineVersions: SublineVersion[] = [];
  totalCount$ = 0;
  isLoading = false;
  private sublineVersionsSubscription!: Subscription;

  constructor(
    private sublinesService: SublinesService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.getVersions({ page: 0, size: 10, sort: 'swissSublineNumber,ASC' });
  }

  getVersions($pagination: TablePagination) {
    this.isLoading = true;
    this.sublineVersionsSubscription = this.sublinesService
      .getSublineVersions(undefined, $pagination.page, $pagination.size, [$pagination.sort!])
      .pipe(
        catchError((err) => {
          this.notificationService.error('LIDI.SUBLINE.NOTIFICATION.FETCH_ERROR');
          this.isLoading = false;
          throw err;
        })
      )
      .subscribe((sublineVersionContainer) => {
        this.sublineVersions = sublineVersionContainer.versions!;
        this.totalCount$ = sublineVersionContainer.totalCount!;
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

  editVersion($event: SublineVersion) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.sublineVersionsSubscription.unsubscribe();
  }
}
