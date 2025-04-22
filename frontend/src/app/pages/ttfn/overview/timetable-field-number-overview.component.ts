import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { TableColumn } from '../../../core/components/table/table-column';
import {
  BusinessOrganisation,
  Status,
  TimetableFieldNumber,
} from '../../../api';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { FormControl, FormGroup } from '@angular/forms';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TableFilterChip } from '../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterSearchSelect } from '../../../core/components/table-filter/config/table-filter-search-select';
import { TableFilterSearchType } from '../../../core/components/table-filter/config/table-filter-search-type';
import { TableFilterMultiSelect } from '../../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterDateSelect } from '../../../core/components/table-filter/config/table-filter-date-select';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../pages';
import { TableService } from '../../../core/components/table/table.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/timetable-field-number-internal.service';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-timetable-field-number-overview',
  templateUrl: './timetable-field-number-overview.component.html',
  imports: [AtlasButtonComponent, TableComponent, RouterOutlet, TranslatePipe],
})
export class TimetableFieldNumberOverviewComponent
  implements OnInit, OnDestroy
{
  private readonly tableFilterConfigIntern = {
    chipSearch: new TableFilterChip(0, 'col-6'),
    searchSelect: new TableFilterSearchSelect<BusinessOrganisation>(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      1,
      'col-3',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    ),
    multiSelectStatus: new TableFilterMultiSelect(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      1,
      'col-3',
      DEFAULT_STATUS_SELECTION
    ),
    dateSelect: new TableFilterDateSelect(1, 'col-3'),
  };

  private getVersionsSubscription?: Subscription;

  tableFilterConfig!: TableFilter<unknown>[][];

  tableColumns: TableColumn<TimetableFieldNumber>[] = [
    { headerTitle: 'TTFN.NUMBER', value: 'number' },
    { headerTitle: 'TTFN.DESCRIPTION', value: 'description' },
    {
      headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER',
      value: 'swissTimetableFieldNumber',
    },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  timetableFieldNumbers: TimetableFieldNumber[] = [];
  totalCount$ = 0;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumberInternalService,
    private route: ActivatedRoute,
    private router: Router,
    private tableService: TableService
  ) {}

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.TTFN
    );
  }

  getOverview(pagination: TablePagination) {
    this.getVersionsSubscription = this.timetableFieldNumbersService
      .getOverview(
        this.tableService.filter.chipSearch.getActiveSearch(),
        undefined,
        this.tableService.filter.searchSelect.getActiveSearch()?.sboid,
        this.tableService.filter.dateSelect.getActiveSearch(),
        this.tableService.filter.multiSelectStatus.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'ttfnid,asc')
      )
      .subscribe((container) => {
        this.timetableFieldNumbers = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  newVersion() {
    this.router
      .navigate(['add'], {
        relativeTo: this.route,
      })
      .then();
  }

  editVersion($event: TimetableFieldNumber) {
    this.router
      .navigate([$event.ttfnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.getVersionsSubscription?.unsubscribe();
  }
}
