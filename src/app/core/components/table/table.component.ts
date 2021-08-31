import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { TableColumn } from './table-column';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<DATATYPE> implements OnInit {
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() tableData!: DATATYPE[];
  @Input() tableCaption!: string;
  @Input() canEdit!: boolean;
  @Input() isLoading = false;

  @Output() newElementEvent = new EventEmitter<DATATYPE>();
  @Output() editElementEvent = new EventEmitter<DATATYPE>();

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  tableDataSrc!: MatTableDataSource<DATATYPE>;

  ngOnInit() {
    this.tableDataSrc = new MatTableDataSource(this.tableData);
    this.tableDataSrc.sort = this.sort;
    this.tableDataSrc.paginator = this.paginator;
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
}
