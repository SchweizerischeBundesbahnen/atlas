import { Component, OnDestroy, OnInit } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { CompaniesService, Company } from '../../../api';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../pages';
import { TableComponent } from '../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-bodi-companies',
  templateUrl: './companies.component.html',
  imports: [TableComponent, RouterOutlet, TranslatePipe],
})
export class CompaniesComponent implements OnInit, OnDestroy {
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

  private tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
  };

  tableFilterConfig!: TableFilter<unknown>[][];

  companies: Company[] = [];
  totalCount = 0;

  private companiesSubscription?: Subscription;

  constructor(
    private companiesService: CompaniesService,
    private route: ActivatedRoute,
    private router: Router,
    private tableService: TableService
  ) {}

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.COMPANIES
    );
  }

  getOverview(pagination: TablePagination) {
    this.companiesSubscription = this.companiesService
      .getCompanies(
        this.tableService.filter.chipSearch.getActiveSearch(),
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
  }
}
