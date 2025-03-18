import { Component, OnDestroy, OnInit } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { TransportCompaniesService, TransportCompany, TransportCompanyStatus } from '../../../api';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { TableService } from '../../../core/components/table/table.service';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterMultiSelect } from '../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../pages';
import { TableComponent } from '../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-bodi-transport-companies',
    templateUrl: './transport-companies.component.html',
    imports: [TableComponent, RouterOutlet, TranslatePipe]
})
export class TransportCompaniesComponent implements OnInit, OnDestroy {
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

  private tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
    multiSelectTransportCompanyStatus: new TableFilterMultiSelect(
      'BODI.TRANSPORT_COMPANIES.TRANSPORT_COMPANY_STATUS.',
      'BODI.TRANSPORT_COMPANIES.STATUS',
      Object.values(TransportCompanyStatus),
      1,
      'col-3',
      [
        TransportCompanyStatus.Current,
        TransportCompanyStatus.OperatingPart,
        TransportCompanyStatus.Operator,
        TransportCompanyStatus.Supervision,
      ]
    ),
  };

  tableFilterConfig!: TableFilter<unknown>[][];

  transportCompanies: TransportCompany[] = [];
  totalCount = 0;

  private transportCompaniesSubscription?: Subscription;

  constructor(
    private transportCompaniesService: TransportCompaniesService,
    private route: ActivatedRoute,
    private router: Router,
    private tableService: TableService
  ) {}

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.TRANSPORT_COMPANIES
    );
  }

  getOverview(pagination: TablePagination) {
    this.transportCompaniesSubscription = this.transportCompaniesService
      .getTransportCompanies(
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectTransportCompanyStatus.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'number,asc')
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
  }
}
