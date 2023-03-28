import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { CompaniesService, Company } from '../../../api';
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
  selector: 'app-bodi-companies',
  templateUrl: './companies.component.html',
})
export class CompaniesComponent implements OnInit, OnDestroy {
  // @ViewChild(TableComponent, { static: true })
  // tableComponent!: TableComponent<Company>;

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

  companies: Company[] = [];
  totalCount = 0;
  isLoading = false;
  private companiesSubscription!: Subscription;
  private routeSubscription!: Subscription;

  constructor(
    private companiesService: CompaniesService,
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
    const storedTableSettings = this.tableSettingsService.getTableSettings(Pages.COMPANIES.path);
    this.getOverview(
      storedTableSettings || {
        page: 0,
        size: 10,
        sort: this.getDefaultSort(),
      }
    );
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(Pages.COMPANIES.path, $paginationAndSearch);
    this.isLoading = true;
    this.companiesSubscription = this.companiesService
      .getCompanies(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, this.getDefaultSort()]
      )
      .subscribe((container) => {
        this.companies = container.objects!;
        this.totalCount = container.totalCount!;
        // this.tableComponent.setTableSettings($paginationAndSearch); TODO: tableSettings
        this.isLoading = false;
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
    this.companiesSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }

  getDefaultSort() {
    return 'uicCode,ASC';
  }
}
