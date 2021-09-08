import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { TableColumn } from './table-column';
import { Sort } from '@angular/material/sort';

@Component({
  selector: 'app-table [tableData][tableColumns][newElementEvent][editElementEvent]',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<DATATYPE> implements AfterViewInit {
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() tableData!: DATATYPE[];
  @Input() tableCaption!: string;
  @Input() canEdit = true;
  @Input() isLoading = false;
  @Input() totalCount!: number;

  @Output() newElementEvent = new EventEmitter<DATATYPE>();
  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() getTableElementsEvent = new EventEmitter();

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  tableDataSrc!: MatTableDataSource<DATATYPE>;
  loading = true;

  ngAfterViewInit() {
    if (this.tableDataSrc !== undefined) {
      this.tableDataSrc.paginator = this.paginator;
      this.tableDataSrc.sort = this.sort;
    }
  }

  getColumnValues(): string[] {
    return this.tableColumns.map((i) => i.value);
  }

  new(): void {
    this.newElementEvent.emit();
  }

  edit(row: DATATYPE) {
    this.editElementEvent.emit(row);
  }

  pageChanged(pageEvent: PageEvent) {
    this.loading = true;
    const pageIndex = pageEvent.pageIndex;
    const pageSize = pageEvent.pageSize;
    this.getTableElementsEvent.emit({ page: pageIndex, size: pageSize });
  }

  sortData(sort: Sort) {
    this.paginator.firstPage();
    const sortElement = sort.active + ',' + sort.direction.toUpperCase();
    this.getTableElementsEvent.emit({ page: 0, size: 10, sort: sortElement });
  }
}
