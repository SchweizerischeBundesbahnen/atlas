import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { NotificationService } from '../../../core/notification/notification.service';
import { SublinesService, Subline, SublineType } from '../../../api';
import { TableSearch } from '../../../core/components/table-search/table-search';
import { TableComponent } from '../../../core/components/table/table.component';

@Component({
  selector: 'app-lidi-sublines',
  templateUrl: './sublines.component.html',
})
export class SublinesComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true }) tableComponent!: TableComponent<Subline>;

  sublinesTableColumns: TableColumn<Subline>[] = [
    { headerTitle: 'LIDI.SUBLINE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.SUBLINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SWISS_SUBLINE_NUMBER', value: 'swissSublineNumber' },
    { headerTitle: 'LIDI.SWISS_LINE_NUMBER', value: 'swissLineNumber' },
    {
      headerTitle: 'LIDI.SUBLINE_TYPE',
      value: 'sublineType',
      translate: { withPrefix: 'LIDI.SUBLINE.TYPES.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'LIDI.BUSINESS_ORGANISATION', value: 'businessOrganisation' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
  ];

  readonly SUBLINE_TYPES: SublineType[] = Object.values(SublineType);
  activeSublineTypes: SublineType[] = [];
  sublines: Subline[] = [];
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
    this.getOverview({ page: 0, size: 10, sort: 'swissSublineNumber,ASC' });
  }

  getOverview($paginationAndSearch: TablePagination & TableSearch) {
    this.isLoading = true;
    this.sublineVersionsSubscription = this.sublinesService
      .getSublines(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.statusChoices,
        this.activeSublineTypes,
        $paginationAndSearch.validOn,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'slnid,ASC']
      )
      .pipe(
        catchError((err) => {
          this.notificationService.error(err, 'LIDI.SUBLINE.NOTIFICATION.FETCH_ERROR');
          this.isLoading = false;
          throw err;
        })
      )
      .subscribe((sublineContainer) => {
        this.sublines = sublineContainer.objects!;
        this.totalCount$ = sublineContainer.totalCount!;
        this.isLoading = false;
      });
  }

  onSublineTypeSelectionChange(): void {
    this.tableComponent.searchData(this.tableComponent.tableSearchComponent.activeSearch);
  }

  editVersion($event: Subline) {
    this.router
      .navigate([$event.slnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.sublineVersionsSubscription.unsubscribe();
  }
}
