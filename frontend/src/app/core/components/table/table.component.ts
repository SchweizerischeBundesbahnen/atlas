import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { TableColumn } from './table-column';
import { TablePagination } from './table-pagination';
import { DateService } from '../../date/date.service';
import { TranslatePipe } from '@ngx-translate/core';
import { TableSearchComponent } from '../table-search/table-search.component';
import { TableSearch } from '../table-search/table-search';

@Component({
  selector: 'app-table [tableData][tableColumns][editElementEvent]',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<DATATYPE> implements AfterViewInit {
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() tableData!: DATATYPE[];
  @Input() canEdit = true;
  @Input() isLoading = false;
  @Input() totalCount!: number;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Input() tableSearchFieldTemplate!: TemplateRef<any>;

  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() getTableElementsEvent = new EventEmitter<TablePagination & TableSearch>();

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild(TableSearchComponent, { static: true }) tableSearchComponent!: TableSearchComponent;

  tableDataSrc!: MatTableDataSource<DATATYPE>;
  loading = true;

  SHOW_TOOLTIP_LENGTH = 20;

  constructor(private dateService: DateService, private translatePipe: TranslatePipe) {}

  ngAfterViewInit() {
    if (this.tableDataSrc !== undefined) {
      this.tableDataSrc.paginator = this.paginator;
      this.tableDataSrc.sort = this.sort;
    }
  }

  getColumnValues(): string[] {
    return this.tableColumns.map((i) => i.value);
  }

  edit(row: DATATYPE) {
    this.editElementEvent.emit(row);
  }

  pageChanged(pageEvent: PageEvent) {
    this.loading = true;
    const pageIndex = pageEvent.pageIndex;
    const pageSize = pageEvent.pageSize;
    this.getTableElementsEvent.emit({
      page: pageIndex,
      size: pageSize,
      sort: `${this.sort.active},${this.sort.direction.toUpperCase()}`,
      searchCriteria: this.tableSearchComponent.searchStrings,
      validOn: this.tableSearchComponent.searchDate,
      statusChoices: this.tableSearchComponent.activeStatuses,
    });
  }

  sortData(sort: Sort) {
    if (this.paginator.pageIndex !== 0) {
      this.paginator.firstPage();
    } else {
      this.getTableElementsEvent.emit({
        page: 0,
        size: this.paginator.pageSize,
        sort: `${sort.active},${sort.direction.toUpperCase()}`,
        searchCriteria: this.tableSearchComponent.searchStrings,
        validOn: this.tableSearchComponent.searchDate,
        statusChoices: this.tableSearchComponent.activeStatuses,
      });
    }
  }

  searchData(search: TableSearch): void {
    if (this.paginator.pageIndex !== 0) {
      this.paginator.firstPage();
    } else {
      this.getTableElementsEvent.emit({
        page: 0,
        size: this.paginator.pageSize,
        sort: `${this.sort.active},${this.sort.direction.toUpperCase()}`,
        searchCriteria: search.searchCriteria,
        validOn: search.validOn,
        statusChoices: search.statusChoices,
      });
    }
  }

  format(column: TableColumn<DATATYPE>, value: string | Date): string {
    if (column.formatAsDate) {
      return DateService.getDateFormatted(value as Date);
    }
    if (column.translate?.withPrefix) {
      return value ? this.translatePipe.transform(column.translate.withPrefix + value) : null;
    }
    return value as string;
  }

  hideTooltip(forText: string | null) {
    if (!forText) {
      return true;
    }
    return forText.length <= this.SHOW_TOOLTIP_LENGTH;
  }
}
