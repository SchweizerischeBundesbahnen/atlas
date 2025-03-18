import {Component, OnDestroy, OnInit} from '@angular/core';
import {TableColumn} from '../../../core/components/table/table-column';
import { Router, RouterOutlet } from '@angular/router';
import {Subscription} from 'rxjs';
import {BusinessOrganisation, ElementType, LidiElementType, Line, LinesService, Status} from '../../../api';
import {TableService} from '../../../core/components/table/table.service';
import {TablePagination} from '../../../core/components/table/table-pagination';
import {addElementsToArrayWhenNotUndefined} from '../../../core/util/arrays';
import {FormControl, FormGroup} from '@angular/forms';
import {TableFilterChip} from '../../../core/components/table-filter/config/table-filter-chip';
import {TableFilterSearchSelect} from '../../../core/components/table-filter/config/table-filter-search-select';
import {TableFilterSearchType} from '../../../core/components/table-filter/config/table-filter-search-type';
import {TableFilterMultiSelect} from '../../../core/components/table-filter/config/table-filter-multiselect';
import {TableFilter} from '../../../core/components/table-filter/config/table-filter';
import {TableFilterDateSelect} from '../../../core/components/table-filter/config/table-filter-date-select';
import {Pages} from '../../pages';
import { TableComponent } from '../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-lidi-lines',
    templateUrl: './lines.component.html',
    imports: [TableComponent, RouterOutlet, TranslatePipe]
})
export class LinesComponent implements OnInit, OnDestroy {
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
    multiSelectLineType: new TableFilterMultiSelect(
      'LIDI.LINE.TYPES.',
      'LIDI.TYPE',
      Object.values(LidiElementType),
      1,
      'col-3'
    ),
    multiSelectStatus: new TableFilterMultiSelect(
      'COMMON.STATUS_TYPES.',
      'COMMON.STATUS',
      Object.values(Status),
      1,
      'col-3',
      [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn]
    ),
    dateSelect: new TableFilterDateSelect(1, 'col-3'),
  };

  private lineVersionsSubscription?: Subscription;

  linesTableColumns: TableColumn<Line>[] = [
    {headerTitle: 'LIDI.LINE.NUMBER', value: 'number'},
    {headerTitle: 'LIDI.LINE.DESCRIPTION', value: 'description'},
    {headerTitle: 'LIDI.TYPE', value: 'lidiElementType', translate: {withPrefix: 'LIDI.LINE.TYPES.'}},
    {headerTitle: 'LIDI.SLNID', value: 'slnid'},
    {headerTitle: 'LIDI.SWISS_LINE_NUMBER', value: 'swissLineNumber'},
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: {withPrefix: 'COMMON.STATUS_TYPES.'},
    },
    {headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true},
    {headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true},
  ];

  tableFilterConfig!: TableFilter<unknown>[][];

  lineVersions: Line[] = [];
  totalCount$ = 0;

  constructor(
    private linesService: LinesService,
    private router: Router,
    private tableService: TableService
  ) {
  }

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.LINES
    );
  }

  getOverview(pagination: TablePagination) {
    this.lineVersionsSubscription = this.linesService
      .getLines(
        undefined,
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatus.getActiveSearch(),
        this.tableService.filter.multiSelectLineType.getActiveSearch(),
        undefined,
        this.tableService.filter.searchSelect.getActiveSearch()?.sboid,
        this.tableService.filter.dateSelect.getActiveSearch(),
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'slnid,asc')
      )
      .subscribe((lineContainer) => {
        this.lineVersions = lineContainer.objects!;
        this.totalCount$ = lineContainer.totalCount!;
      });
  }

  editVersion($event: Line) {
    let pathToNavigate = Pages.LINES.path;
    if ($event.elementType == ElementType.Subline) {
      pathToNavigate = Pages.SUBLINES.path;
    }
    this.router.navigate([Pages.LIDI.path, pathToNavigate, $event.slnid]).then();
  }

  ngOnDestroy() {
    this.lineVersionsSubscription?.unsubscribe();
  }
}
