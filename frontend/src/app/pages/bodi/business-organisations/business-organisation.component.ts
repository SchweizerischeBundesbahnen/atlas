import { ChangeDetectionStrategy, Component, inject, OnDestroy, signal } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { BusinessOrganisation, Status } from '../../../api';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { TableService } from '../../../core/components/table/table.service';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterMultiSelect } from '../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterDateSelect } from '../../../core/components/table-filter/config/table-filter-date-select';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../pages';
import { TableComponent } from '../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';

@Component({
  selector: 'atlas-bodi-business-organisations',
  templateUrl: './business-organisation.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [TableComponent, TranslatePipe],
  providers: [TranslatePipe],
})
export class BusinessOrganisationComponent implements OnDestroy {
  private readonly businessOrganisationService = inject(BusinessOrganisationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly businessOrganisationLanguageService = inject(BusinessOrganisationLanguageService);
  private readonly tableService = inject(TableService);

  tableColumns: TableColumn<BusinessOrganisation>[] = this.getColumns();
  tableFilterConfig!: TableFilter<unknown>[][];
  businessOrganisations = signal<BusinessOrganisation[]>([]);
  totalCount = signal(0);

  private tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
    multiSelectStatus: new TableFilterMultiSelect(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      1,
      'filter-width-quarter',
      DEFAULT_STATUS_SELECTION
    ),
    dateSelect: new TableFilterDateSelect(1, 'filter-width-quarter'),
  };
  private businessOrganisationsSubscription?: Subscription;
  private langChangeSubscription: Subscription;

  constructor() {
    this.langChangeSubscription = this.businessOrganisationLanguageService
      .languageChanged()
      .subscribe(() => (this.tableColumns = this.getColumns()));

    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.BUSINESS_ORGANISATIONS
    );
  }

  getOverview(pagination: TablePagination) {
    this.businessOrganisationsSubscription = this.businessOrganisationService
      .getAllBusinessOrganisations(
        this.tableService.filter.chipSearch.getActiveSearch(),
        undefined,
        this.tableService.filter.dateSelect.getActiveSearch(),
        this.tableService.filter.multiSelectStatus.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, this.getDefaultSort())
      )
      .subscribe((container) => {
        this.businessOrganisations.set(container.objects!);
        this.totalCount.set(container.totalCount!);
      });
  }

  editVersion($event: BusinessOrganisation) {
    this.router
      .navigate([$event.sboid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.businessOrganisationsSubscription?.unsubscribe();
    this.langChangeSubscription.unsubscribe();
  }

  getDefaultSort() {
    return this.getCurrentLanguageDescription() + ',asc';
  }

  private getCurrentLanguageAbbreviation() {
    return this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation();
  }

  private getCurrentLanguageDescription() {
    return this.businessOrganisationLanguageService.getCurrentLanguageDescription();
  }

  private getColumns(): TableColumn<BusinessOrganisation>[] {
    return [
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
        value: this.getCurrentLanguageDescription(),
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
        value: this.getCurrentLanguageAbbreviation(),
      },
      { headerTitle: 'BODI.BUSINESS_ORGANISATION.SBOID', value: 'sboid' },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
        value: 'organisationNumber',
      },
      {
        headerTitle: 'COMMON.VALID_FROM',
        value: 'validFrom',
        formatAsDate: true,
      },
      { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    ];
  }
}
