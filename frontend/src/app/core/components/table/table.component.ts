import { Component, EventEmitter, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import { MatSort, Sort, SortDirection } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { TableColumn } from './table-column';
import { DateService } from '../../date/date.service';
import { TranslatePipe } from '@ngx-translate/core';
import { TableSearchComponent } from '../table-search/table-search.component';
import { TableSearch } from '../table-search/table-search';
import { TableSettings } from './table-settings';
import { SearchStatusType } from '../table-search/base-table-search';

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
  @Input() pageSizeOptions: number[] = [5, 10, 25, 100];
  @Input() sortingDisabled = false;

  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() getTableElementsEvent = new EventEmitter<TableSettings>();

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  @ViewChild(TableSearchComponent) tableSearchComponent!: TableSearchComponent;
  @Input() searchTextColumnStyle = 'col-4';
  @Input() displayStatusSearch = true;
  @Input() displayValidOnSearch = true;
  @Input() displayBusinessOrganisationSearch = true;
  @Input() loadTableSearch = true;
  @Input() searchStatusType: SearchStatusType = 'DEFAULT_STATUS';

  loading = true;

  SHOW_TOOLTIP_LENGTH = 20;

  constructor(private dateService: DateService, private translatePipe: TranslatePipe) {}

  getColumnValues(): string[] {
    return this.tableColumns.map((i) => i.value as string);
  }

  edit(row: DATATYPE) {
    this.editElementEvent.emit(row);
  }

  pageChanged(pageEvent: PageEvent) {
    this.loading = true;
    const pageIndex = pageEvent.pageIndex;
    const pageSize = pageEvent.pageSize;
    this.getElementsSearched({
      ...this.tableSearchComponent?.activeSearch,
      page: pageIndex,
      size: pageSize,
      sort: `${this.sort.active},${this.sort.direction.toUpperCase()}`,
    });
  }

  sortData(sort: Sort) {
    if (this.paginator.pageIndex !== 0) {
      this.paginator.firstPage();
    } else {
      this.getElementsSearched({
        ...this.tableSearchComponent.activeSearch,
        page: 0,
        size: this.paginator.pageSize,
        sort: `${sort.active},${sort.direction.toUpperCase()}`,
      });
    }
  }

  searchData(search: TableSearch): void {
    if (this.paginator.pageIndex !== 0) {
      this.paginator.firstPage();
    } else {
      this.getElementsSearched({
        page: 0,
        size: this.paginator.pageSize,
        sort: `${this.sort.active},${this.sort.direction.toUpperCase()}`,
        ...search,
      });
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

    this.tableSearchComponent.activeStatuses = tableSettings.statusChoices || [];
    this.tableSearchComponent.restoreBusinessOrganisation(tableSettings.boChoice);

    this.tableSearchComponent.searchDate = tableSettings.validOn;
    this.tableSearchComponent.dateControl.setValue(tableSettings.validOn);

    this.tableSearchComponent.activeSearch = tableSettings;
  }

  private getElementsSearched(tableSettings: TableSettings) {
    if (this.tableSearchComponent) {
      this.tableSearchComponent.activeSearch = tableSettings;
    }
    this.getTableElementsEvent.emit(tableSettings);
  }
}
