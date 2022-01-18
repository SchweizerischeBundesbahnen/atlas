import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subscription } from 'rxjs';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { NotificationService } from '../../../core/notification/notification.service';
import { Line, LinesService } from '../../../api';
import { Pages } from '../../pages';
import { TableSearch } from '../../../core/components/table-search/table-search';
import { TableComponent } from '../../../core/components/table/table.component';

@Component({
  selector: 'app-lidi-lines',
  templateUrl: './lines.component.html',
  styleUrls: ['./lines.component.scss'],
})
export class LinesComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true }) tableComponent!: TableComponent<Line>;

  linesTableColumns: TableColumn<Line>[] = [
    { headerTitle: 'LIDI.SWISS_LINE_NUMBER', value: 'swissLineNumber' },
    { headerTitle: 'LIDI.LINE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.LINE.DESCRIPTION', value: 'description' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'LIDI.TYPE', value: 'type', translate: { withPrefix: 'LIDI.LINE.TYPES.' } },
    { headerTitle: 'LIDI.BUSINESS_ORGANISATION', value: 'businessOrganisation' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  readonly LINE_TYPES: Line.TypeEnum[] = Object.values(Line.TypeEnum);
  activeLineTypes: Line.TypeEnum[] = [];
  lineVersions: Line[] = [];
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
    this.getOverview({ page: 0, size: 10, sort: 'swissLineNumber,ASC' });
  }

  getOverview($paginationAndSearch: TablePagination & TableSearch) {
    this.isLoading = true;
    this.lineVersionsSubscription = this.linesService
      .getLines(
        undefined,
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.statusChoices,
        this.activeLineTypes,
        $paginationAndSearch.validOn,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'slnid,ASC']
      )
      .pipe(
        catchError((err) => {
          this.notificationService.error('LIDI.LINE.NOTIFICATION.FETCH_ERROR');
          this.isLoading = false;
          throw err;
        })
      )
      .subscribe((lineContainer) => {
        this.lineVersions = lineContainer.objects!;
        this.totalCount$ = lineContainer.totalCount!;
        this.isLoading = false;
      });
  }

  onLineTypeSelectionChange(): void {
    this.tableComponent.searchData(this.tableComponent.tableSearchComponent.activeSearch);
  }

  newVersion() {
    this.router
      .navigate([Pages.LINES.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }

  editVersion($event: Line) {
    this.router
      .navigate([Pages.LINES.path, $event.slnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.lineVersionsSubscription.unsubscribe();
  }
}
