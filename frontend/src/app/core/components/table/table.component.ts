import { Component, contentChild, inject, input, OnInit, output, signal, TemplateRef } from '@angular/core';
import { MatSort, MatSortHeader, Sort, SortDirection } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { TableColumn } from './table-column';
import { TableService } from './table.service';
import { TablePagination } from './table-pagination';
import { ColumnDropDownEvent } from './column-drop-down-event';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { TableFilter } from '../table-filter/config/table-filter';
import { LoadingSpinnerComponent } from '../loading-spinner/loading-spinner.component';
import { NgClass, NgTemplateOutlet } from '@angular/common';
import { TableFilterComponent } from '../table-filter/table-filter.component';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatNoDataRow,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { MouseOverTitleDirective } from './directive/mouse-over-title.directive';
import { SelectComponent } from '../../form-components/select/select.component';
import { AtlasButtonComponent } from '../button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ShowTitlePipe } from './pipe/show-title.pipe';
import { FormatPipe } from './pipe/format.pipe';

@Component({
  selector: 'atlas-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  imports: [
    LoadingSpinnerComponent,
    TableFilterComponent,
    MatTable,
    MatSort,
    NgClass,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatSortHeader,
    MatCheckbox,
    MatCellDef,
    MatCell,
    MouseOverTitleDirective,
    SelectComponent,
    AtlasButtonComponent,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatNoDataRow,
    MatPaginator,
    TranslatePipe,
    ShowTitlePipe,
    FormatPipe,
    NgTemplateOutlet,
  ],
})
export class TableComponent<DATATYPE> implements OnInit {
  readonly checkBoxSelection = input(new SelectionModel<DATATYPE>(true, []));
  readonly tableFilterConfig = input<TableFilter<unknown>[][]>([]);
  readonly tableColumns = input.required<TableColumn<DATATYPE>[]>();
  readonly totalCount = input<number>();
  readonly pageSizeOptions = input<number[]>([5, 10, 25, 100]);
  readonly sortingDisabled = input(false);
  readonly showTableFilter = input(true);
  readonly showPaginator = input(true);
  readonly checkBoxModeEnabled = input(false);
  readonly additionalTableStyleClass = input('');
  readonly tableData = input.required<DATATYPE[]>();

  readonly editElementEvent = output<DATATYPE>();
  readonly tableChanged = output<TablePagination>();
  readonly tableInitialized = output<TablePagination>();
  readonly changeDropdownEvent = output<ColumnDropDownEvent<DATATYPE>>();
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  readonly buttonClickEvent = output<any>();
  readonly checkedBoxEvent = output<SelectionModel<DATATYPE>>();
  isLoading = signal<boolean>(false);

  customCell = contentChild(TemplateRef);

  private readonly tableService = inject(TableService);

  constructor() {
    // toObservable(this.tableData).subscribe(() => this.isLoading.set(false));
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
    return this.tableColumns().map((i) => (i.columnDef ?? i.value) as string);
  }

  edit(row: DATATYPE) {
    if (this.checkBoxModeEnabled()) {
      this.checkBoxSelection().toggle(row);
      this.checkedBoxEvent.emit(this.checkBoxSelection());
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
    const numSelected = this.checkBoxSelection().selected.length;
    return numSelected === this.pageSize || numSelected === this.totalCount();
  }

  toggleAll() {
    if (this.isAllSelected()) {
      this.checkBoxSelection().clear();
    } else {
      this.tableData().forEach((row) => this.checkBoxSelection().select(row));
    }
    this.checkedBoxEvent.emit(this.checkBoxSelection());
  }

  toggleCheckBox($event: MatCheckboxChange, row: DATATYPE) {
    if ($event) {
      this.checkBoxSelection().toggle(row);
    }
    this.checkedBoxEvent.emit(this.checkBoxSelection());
  }

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  stopPropagation($event: any) {
    if (!this.checkBoxModeEnabled()) {
      $event.stopPropagation();
    }
  }

  private emitTableChangedEvent(): void {
    // this.isLoading.set(true);
    this.tableChanged.emit({
      page: this.pageIndex,
      size: this.pageSize,
      sort: this.sortString,
    });
  }

  protected isActionColumn(column: TableColumn<DATATYPE>): boolean {
    return !column.checkbox && !column.dropdown && !column.button && !column.customCell && !column.icon;
  }
}
