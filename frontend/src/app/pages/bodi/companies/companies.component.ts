import { Component, OnDestroy } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { CompaniesService, Company } from '../../../api';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import {
  FilterType,
  getActiveSearchForChip,
  TableFilterChip,
} from '../../../core/components/table-filter/table-filter-config';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';

@Component({
  selector: 'app-bodi-companies',
  templateUrl: './companies.component.html',
  providers: [TableService],
})
export class CompaniesComponent implements OnDestroy {
  tableColumns: TableColumn<Company>[] = [
    { headerTitle: 'BODI.COMPANIES.UIC_CODE', value: 'uicCode' },
    {
      headerTitle: 'BODI.COMPANIES.SHORT_NAME',
      value: 'shortName',
    },
    {
      headerTitle: 'BODI.COMPANIES.NAME',
      value: 'name',
    },
    { headerTitle: 'BODI.COMPANIES.COUNTRY_CODE', value: 'countryCodeIso' },
    { headerTitle: 'BODI.COMPANIES.URL', value: 'url' },
  ];

  readonly tableFilterConfig: [[TableFilterChip]] = [
    [
      {
        filterType: FilterType.CHIP_SEARCH,
        elementWidthCssClass: 'col-6',
        activeSearch: [],
      },
    ],
  ];

  companies: Company[] = [];
  totalCount = 0;

  private companiesSubscription?: Subscription;
  private routeSubscription: Subscription;

  constructor(
    private companiesService: CompaniesService,
    private route: ActivatedRoute,
    private router: Router,
    private routeToDialogService: RouteToDialogService,
    private readonly tableService: TableService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => {
        this.getOverview({
          page: this.tableService.pageIndex,
          size: this.tableService.pageSize,
          sort: this.tableService.sortString,
        });
      });
  }

  getOverview(pagination: TablePagination) {
    this.companiesSubscription = this.companiesService
      .getCompanies(
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'uicCode,asc')
      )
      .subscribe((container) => {
        this.companies = container.objects!;
        this.totalCount = container.totalCount!;
      });
  }

  editVersion($event: Company) {
    this.router
      .navigate([$event.uicCode], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.companiesSubscription?.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
