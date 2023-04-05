import { Component, OnDestroy } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TransportCompaniesService, TransportCompany, TransportCompanyStatus } from '../../../api';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import {
  FilterType,
  getActiveSearch,
  getActiveSearchForChip,
  TableFilterChip,
  TableFilterMultiSelect,
} from '../../../core/components/table-filter/table-filter-config';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { TableService } from '../../../core/components/table/table.service';

@Component({
  selector: 'app-bodi-transport-companies',
  templateUrl: './transport-companies.component.html',
  providers: [TableService],
})
export class TransportCompaniesComponent implements OnDestroy {
  tableColumns: TableColumn<TransportCompany>[] = [
    { headerTitle: 'BODI.TRANSPORT_COMPANIES.NUMBER', value: 'number' },
    {
      headerTitle: 'BODI.TRANSPORT_COMPANIES.ABBREVIATION',
      value: 'abbreviation',
    },
    {
      headerTitle: 'BODI.TRANSPORT_COMPANIES.BUSINESS_REGISTER_NAME',
      value: 'businessRegisterName',
    },
    { headerTitle: 'BODI.TRANSPORT_COMPANIES.DESCRIPTION', value: 'description' },
    { headerTitle: 'BODI.TRANSPORT_COMPANIES.ENTERPRISE_ID', value: 'enterpriseId' },
    {
      headerTitle: 'BODI.TRANSPORT_COMPANIES.STATUS',
      value: 'transportCompanyStatus',
      translate: { withPrefix: 'BODI.TRANSPORT_COMPANIES.TRANSPORT_COMPANY_STATUS.' },
    },
  ];

  readonly tableFilterConfig: [
    [TableFilterChip],
    [TableFilterMultiSelect<TransportCompanyStatus>]
  ] = [
    [
      {
        filterType: FilterType.CHIP_SEARCH,
        elementWidthCssClass: 'col-6',
        activeSearch: [],
      },
    ],
    [
      {
        filterType: FilterType.MULTI_SELECT,
        elementWidthCssClass: 'col-3',
        activeSearch: [
          TransportCompanyStatus.Current,
          TransportCompanyStatus.OperatingPart,
          TransportCompanyStatus.Operator,
          TransportCompanyStatus.Supervision,
        ],
        labelTranslationKey: 'BODI.TRANSPORT_COMPANIES.STATUS',
        typeTranslationKeyPrefix: 'BODI.TRANSPORT_COMPANIES.TRANSPORT_COMPANY_STATUS.',
        selectOptions: Object.values(TransportCompanyStatus),
      },
    ],
  ];

  transportCompanies: TransportCompany[] = [];
  totalCount = 0;

  private transportCompaniesSubscription?: Subscription;
  private routeSubscription: Subscription;

  constructor(
    private transportCompaniesService: TransportCompaniesService,
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
    this.transportCompaniesSubscription = this.transportCompaniesService
      .getTransportCompanies(
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        getActiveSearch(this.tableFilterConfig[1][0]),
        pagination.page,
        pagination.size,
        [pagination.sort!, 'number,asc']
      )
      .subscribe((container) => {
        this.transportCompanies = container.objects!;
        this.totalCount = container.totalCount!;
      });
  }

  editVersion($event: TransportCompany) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.transportCompaniesSubscription?.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
