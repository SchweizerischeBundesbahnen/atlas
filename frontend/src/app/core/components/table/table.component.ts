import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Sort, SortDirection, MatSort, MatSortHeader } from '@angular/material/sort';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { TableColumn } from './table-column';
import { TableService } from './table.service';
import { TablePagination } from './table-pagination';
import { ColumnDropDownEvent } from './column-drop-down-event';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckboxChange, MatCheckbox } from '@angular/material/checkbox';
import { TableFilter } from '../table-filter/config/table-filter';
import { LoadingSpinnerComponent } from '../loading-spinner/loading-spinner.component';
import { NgIf, NgClass, NgFor, NgSwitch, NgSwitchCase } from '@angular/common';
import { TableFilterComponent } from '../table-filter/table-filter.component';
import { MatTable, MatColumnDef, MatHeaderCellDef, MatHeaderCell, MatCellDef, MatCell, MatHeaderRowDef, MatHeaderRow, MatRowDef, MatRow, MatNoDataRow } from '@angular/material/table';
import { MouseOverTitleDirective } from './directive/mouse-over-title.directive';
import { SelectComponent } from '../../form-components/select/select.component';
import { AtlasButtonComponent } from '../button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ShowTitlePipe } from './pipe/show-title.pipe';
import { FormatPipe } from './pipe/format.pipe';

@Component({
    selector: 'app-table [tableData][tableColumns][editElementEvent]',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss'],
    imports: [LoadingSpinnerComponent, NgIf, TableFilterComponent, MatTable, MatSort, NgClass, NgFor, MatColumnDef, MatHeaderCellDef, MatHeaderCell, MatSortHeader, MatCheckbox, NgSwitch, NgSwitchCase, MatCellDef, MatCell, MouseOverTitleDirective, SelectComponent, AtlasButtonComponent, MatHeaderRowDef, MatHeaderRow, MatRowDef, MatRow, MatNoDataRow, MatPaginator, TranslatePipe, ShowTitlePipe, FormatPipe]
})
export class TableComponent<DATATYPE> implements OnInit {
  @Input() checkBoxSelection = new SelectionModel<DATATYPE>(true, []);
  @Input() tableFilterConfig: TableFilter<unknown>[][] = [];
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() canEdit = true;
  @Input() totalCount!: number;
  @Input() pageSizeOptions: number[] = [5, 10, 25, 100];
  @Input() sortingDisabled = false;
  @Input() showTableFilter = true;
  @Input() showPaginator = true;
  @Input() checkBoxModeEnabled = false;
  @Input() additionalTableStyleClass!: string;

  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() tableChanged = new EventEmitter<TablePagination>();
  @Output() tableInitialized: EventEmitter<TablePagination> = new EventEmitter<TablePagination>();
  @Output() changeDropdownEvent = new EventEmitter<ColumnDropDownEvent>();
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Output() buttonClickEvent = new EventEmitter<any>();
  @Output() checkedBoxEvent = new EventEmitter<SelectionModel<DATATYPE>>();
  isLoading = true;

  constructor(private readonly tableService: TableService) {}

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
    this.tableInitialized.emit({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
    });
  }

  getColumnDefs(): string[] {
    return this.tableColumns.map((i) => (i.columnDef ?? i.value) as string);
  }

  edit(row: DATATYPE) {
    if (this.checkBoxModeEnabled) {
      this.checkBoxSelection.toggle(row);
      this.checkedBoxEvent.emit(this.checkBoxSelection);
    } else {
      this.editElementEvent.emit(row);
    }
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

  isAllSelected() {
    const numSelected = this.checkBoxSelection.selected.length;
    return numSelected === this.pageSize || numSelected === this.totalCount;
  }

  toggleAll() {
    if (this.isAllSelected()) {
      this.checkBoxSelection.clear();
    } else {
      this.tableData.forEach((row) => this.checkBoxSelection.select(row));
    }
    this.checkedBoxEvent.emit(this.checkBoxSelection);
  }

  toggleCheckBox($event: MatCheckboxChange, row: DATATYPE) {
    if ($event) {
      this.checkBoxSelection.toggle(row);
    }
    this.checkedBoxEvent.emit(this.checkBoxSelection);
  }

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  stopPropagation($event: any) {
    if (!this.checkBoxModeEnabled) {
      $event.stopPropagation();
    }
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
