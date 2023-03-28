import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { TransportCompaniesService, TransportCompany, TransportCompanyStatus } from '../../../api';
import { TableComponent } from '../../../core/components/table/table.component';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import { Pages } from '../../pages';
import { filter } from 'rxjs/operators';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';

@Component({
  selector: 'app-bodi-transport-companies',
  templateUrl: './transport-companies.component.html',
})
export class TransportCompaniesComponent implements OnInit, OnDestroy {
  readonly STATUS_TYPES: TransportCompanyStatus[] = Object.values(TransportCompanyStatus);
  activeStatusTypes: TransportCompanyStatus[] = [];

  // @ViewChild(TableComponent, { static: true })
  // tableComponent!: TableComponent<TransportCompany>;

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

  transportCompanies: TransportCompany[] = [];
  totalCount = 0;
  isLoading = false;
  private transportCompaniesSubscription!: Subscription;
  private routeSubscription!: Subscription;

  constructor(
    private transportCompaniesService: TransportCompaniesService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private tableSettingsService: TableSettingsService,
    private routeToDialogService: RouteToDialogService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());
  }

  ngOnInit(): void {
    const storedTableSettings = this.tableSettingsService.getTableSettings(
      Pages.TRANSPORT_COMPANIES.path
    );
    this.getOverview(
      storedTableSettings || {
        page: 0,
        size: 10,
        sort: this.getDefaultSort(),
        statusTypes: [
          TransportCompanyStatus.Current,
          TransportCompanyStatus.OperatingPart,
          TransportCompanyStatus.Operator,
          TransportCompanyStatus.Supervision,
        ],
      }
    );
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(
      Pages.TRANSPORT_COMPANIES.path,
      $paginationAndSearch
    );
    this.isLoading = true;
    this.transportCompaniesSubscription = this.transportCompaniesService
      .getTransportCompanies(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.statusTypes,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, this.getDefaultSort()]
      )
      .subscribe((container) => {
        this.transportCompanies = container.objects!;
        this.totalCount = container.totalCount!;
        // this.tableComponent.setTableSettings($paginationAndSearch); TODO: tableSettings
        this.activeStatusTypes = $paginationAndSearch.statusTypes;
        this.isLoading = false;
      });
  }

  onStatusSelectionChange(): void {
    // this.tableComponent.searchData({
    //   //...this.tableComponent.tableSearchComponent.activeSearch,
    //   statusTypes: this.activeStatusTypes,
    // }); TODO: tableFilter
  }

  editVersion($event: TransportCompany) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.transportCompaniesSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }

  getDefaultSort() {
    return 'number,ASC';
  }
}
