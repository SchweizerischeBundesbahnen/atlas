import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Sort, SortDirection } from '@angular/material/sort';
import { PageEvent } from '@angular/material/paginator';
import { TableColumn } from './table-column';
import { DateService } from '../../date/date.service';
import { TranslatePipe } from '@ngx-translate/core';
import { TableFilterConfig } from '../table-filter/table-filter-config';
import { TableService } from './table.service';
import { TablePagination } from './table-pagination';

@Component({
  selector: 'app-table [tableData][tableColumns][editElementEvent]',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<DATATYPE> implements OnInit {
  @Input()
  set tableData(data: DATATYPE[]) {
    this._tableData = data;
    this.isLoading = false;
  }
  get tableData(): DATATYPE[] {
    return this._tableData;
  }

  @Input() tableFilterConfig: TableFilterConfig<unknown>[][] = [];
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() canEdit = true;
  @Input() totalCount!: number;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  // @Input() tableSearchFieldTemplate!: TemplateRef<any>;
  @Input() pageSizeOptions: number[] = [5, 10, 25, 100];
  @Input() sortingDisabled = false;

  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() getTableElementsEvent = new EventEmitter<TablePagination>();

  // @ViewChild(MatSort, { static: true }) sort!: MatSort;
  // @ViewChild(MatPaginator, { static: true }) private paginator!: MatPaginator;

  // @Input() searchTextColumnStyle = 'col-4';
  // @Input() displayStatusSearch = true;
  // @Input() displayValidOnSearch = true;
  // @Input() displayBusinessOrganisationSearch = true;
  @Input() showTableFilter = true;
  // @Input() searchStatusType: SearchStatusType = 'DEFAULT_STATUS';

  isLoading = false;
  SHOW_TOOLTIP_LENGTH = 20;

  private _tableData: DATATYPE[] = [];

  constructor(
    private dateService: DateService,
    private translatePipe: TranslatePipe,
    private readonly tableService: TableService
  ) {}

  ngOnInit() {
    this.tableService.sortActive = this.sortingDisabled ? '' : this.tableColumns[0].value!;
    this.getElementsSearched({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
      // filterConfig: this.tableFilterConfig,
    });
  }

  get pageSize(): number {
    return this.tableService.pageSize;
  }

  get pageIndex(): number {
    return this.tableService.pageIndex;
  }

  get sortActive(): string {
    return this.tableService.sortActive;
  }

  get sortDirection(): SortDirection {
    return this.tableService.sortDirection;
  }

  get sortString(): string | undefined {
    return this.tableService.sortString;
  }

  getColumnValues(): string[] {
    return this.tableColumns.map((i) => i.value as string);
  }

  edit(row: DATATYPE) {
    this.editElementEvent.emit(row);
  }

  pageChanged(pageEvent: PageEvent) {
    this.tableService.pageSize = pageEvent.pageSize;
    this.tableService.pageIndex = pageEvent.pageIndex;

    this.getElementsSearched({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
      // filterConfig: this.tableFilterConfig,
    });
  }

  sortData(sort: Sort) {
    this.tableService.sortActive = sort.active;
    this.tableService.sortDirection = sort.direction;

    if (this.pageIndex !== 0) {
      this.tableService.pageIndex = 0;
    }

    this.getElementsSearched({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
      // filterConfig: this.tableFilterConfig,
    });
  }

  searchData(): void {
    if (this.pageIndex !== 0) {
      this.tableService.pageIndex = 0;
    }

    this.getElementsSearched({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
      // filterConfig: search,
    });
  }

  showTitle(column: TableColumn<DATATYPE>, value: string | Date): string {
    const content = this.format(column, value);
    const hideTooltip = this.hideTooltip(content);
    return !hideTooltip ? content : '';
  }

  format(column: TableColumn<DATATYPE>, value: string | Date): string {
    if (column.formatAsDate) {
      return DateService.getDateFormatted(value as Date);
    }
    if (column.translate?.withPrefix) {
      return value ? this.translatePipe.transform(column.translate.withPrefix + value) : null;
    }
    if (column.callback) {
      return column.callback(value);
    }
    return value as string;
  }

  hideTooltip(forText: string | null) {
    if (!forText) {
      return true;
    }
    return forText.length <= this.SHOW_TOOLTIP_LENGTH;
  }

  private getElementsSearched(tableSettings: TablePagination) {
    this.isLoading = true;
    this.getTableElementsEvent.emit(tableSettings);
  }
}
