import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Sort, SortDirection } from '@angular/material/sort';
import { PageEvent } from '@angular/material/paginator';
import { TableColumn } from './table-column';
import { DateService } from '../../date/date.service';
import { TranslatePipe } from '@ngx-translate/core';
import { TableFilterConfig } from '../table-filter/table-filter-config';
import { TableService } from './table.service';
import { TablePagination } from './table-pagination';
import { ColumnDropDownEvent } from './column-drop-down-event';
import { isEmpty } from '../../util/strings';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckboxChange } from '@angular/material/checkbox';

@Component({
  selector: 'app-table [tableData][tableColumns][editElementEvent]',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<DATATYPE> implements OnInit {
  @Input() checkBoxSelection = new SelectionModel<DATATYPE>(true, []);
  @Input() tableFilterConfig: TableFilterConfig<unknown>[][] = [];
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() canEdit = true;
  @Input() totalCount!: number;
  @Input() pageSizeOptions: number[] = [5, 10, 25, 100];
  @Input() sortingDisabled = false;
  @Input() showTableFilter = true;
  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() tableChanged = new EventEmitter<TablePagination>();
  @Output() tableInitialized: EventEmitter<TablePagination> = new EventEmitter<TablePagination>();
  @Output() changeDropdownEvent = new EventEmitter<ColumnDropDownEvent>();
  @Output() checkedBoxEvent = new EventEmitter<SelectionModel<DATATYPE>>();
  isLoading = false;
  SHOW_TOOLTIP_LENGTH = 20;

  constructor(
    private dateService: DateService,
    private translatePipe: TranslatePipe,
    private readonly tableService: TableService
  ) {}

  private _tableData: DATATYPE[] = [];

  get tableData(): DATATYPE[] {
    return this._tableData;
  }

  @Input()
  set tableData(data: DATATYPE[]) {
    this._tableData = data;
    this.isLoading = false;
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

  ngOnInit() {
    // set default sorting
    if (this.sortingDisabled) {
      this.tableService.sortActive = '';
    } else if (isEmpty(this.sortActive)) {
      this.tableService.sortActive = this.tableColumns[0].value!;
    }
    this.tableInitialized.emit({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
    });
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

    this.emitTableChangedEvent();
  }

  sortData(sort: Sort) {
    this.tableService.sortActive = sort.active;
    this.tableService.sortDirection = sort.direction;

    if (this.pageIndex !== 0) {
      this.tableService.pageIndex = 0;
    }

    this.emitTableChangedEvent();
  }

  searchData(): void {
    if (this.pageIndex !== 0) {
      this.tableService.pageIndex = 0;
    }

    this.emitTableChangedEvent();
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

  isAllSelected() {
    const numSelected = this.checkBoxSelection.selected.length;
    return numSelected === this.pageSize || numSelected === this.totalCount;
  }

  toggleAll() {
    this.isAllSelected()
      ? this.checkBoxSelection.clear()
      : this.tableData.forEach((row) => this.checkBoxSelection.select(row));
    this.checkedBoxEvent.emit(this.checkBoxSelection);
  }

  toggleCheckBox($event: MatCheckboxChange, row: DATATYPE) {
    $event ? this.checkBoxSelection.toggle(row) : null;
    this.checkedBoxEvent.emit(this.checkBoxSelection);
  }

  private emitTableChangedEvent(): void {
    this.isLoading = true;
    this.tableChanged.emit({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
    });
  }
}
