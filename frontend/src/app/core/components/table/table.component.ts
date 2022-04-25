import { Component, EventEmitter, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import { MatSort, Sort, SortDirection } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { TableColumn } from './table-column';
import { DateService } from '../../date/date.service';
import { TranslatePipe } from '@ngx-translate/core';
import { TableSearchComponent } from '../table-search/table-search.component';
import { TableSearch } from '../table-search/table-search';
import { TableSettings } from './table-settings';

@Component({
  selector: 'app-table [tableData][tableColumns][editElementEvent]',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<DATATYPE> {
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() tableData!: DATATYPE[];
  @Input() canEdit = true;
  @Input() isLoading = false;
  @Input() totalCount!: number;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Input() tableSearchFieldTemplate!: TemplateRef<any>;

  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() getTableElementsEvent = new EventEmitter<TableSettings>();

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild(TableSearchComponent, { static: true }) tableSearchComponent!: TableSearchComponent;

  loading = true;

  SHOW_TOOLTIP_LENGTH = 20;

  constructor(private dateService: DateService, private translatePipe: TranslatePipe) {}

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
      const currentSearch = {
        page: 0,
        size: this.paginator.pageSize,
        sort: `${this.sort.active},${this.sort.direction.toUpperCase()}`,
        ...search,
      };
      this.tableSearchComponent.activeSearch = currentSearch;
      this.getTableElementsEvent.emit(currentSearch);
    }
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
    return value as string;
  }

  hideTooltip(forText: string | null) {
    if (!forText) {
      return true;
    }
    return forText.length <= this.SHOW_TOOLTIP_LENGTH;
  }

  setTableSettings(tableSettings: TableSettings) {
    this.paginator.pageIndex = tableSettings.page;
    this.paginator.pageSize = tableSettings.size;

    if (tableSettings.sort) {
      const sorting = tableSettings.sort.split(',');
      this.sort.active = sorting[0];
      this.sort.direction = sorting[1].toLowerCase() as SortDirection;
      this.sort._stateChanges.next();
    }

    this.tableSearchComponent.searchStrings = tableSettings.searchCriteria || [];
    this.tableSearchComponent.activeSearch.searchCriteria = this.tableSearchComponent.searchStrings;

    this.tableSearchComponent.activeStatuses = tableSettings.statusChoices || [];
    this.tableSearchComponent.activeSearch.statusChoices = this.tableSearchComponent.activeStatuses;

    this.tableSearchComponent.searchDate = tableSettings.validOn;
    this.tableSearchComponent.activeSearch.validOn = tableSettings.validOn;
    this.tableSearchComponent.validOnInput.nativeElement.value = tableSettings.validOn
      ? DateService.getDateFormatted(tableSettings.validOn)
      : '';
  }
}
