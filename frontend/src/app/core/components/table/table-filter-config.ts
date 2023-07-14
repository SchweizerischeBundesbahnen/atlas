import { TableFilter } from '../table-filter/config/table-filter';
import { Page } from '../../model/page';
import { TableFilters } from './table-filters-type';

export class TableFilterConfig {
  filters: TableFilters;
  page: Page;

  constructor(settings: TableFilters, page: Page) {
    this.filters = settings;
    this.page = page;
  }

  get settingsInRowRepresentation() {
    const rowRepresentation: TableFilter<unknown>[][] = [];
    Object.values(this.filters).forEach((filter) => {
      rowRepresentation[filter.row] = rowRepresentation[filter.row] || [];
      rowRepresentation[filter.row].push(filter);
    });
    return rowRepresentation;
  }

  enableFilters() {
    Object.values(this.filters).forEach((filter) => {
      filter.disabled = false;
    });
  }

  disableFilters() {
    Object.values(this.filters).forEach((filter) => {
      filter.disabled = true;
    });
  }
}
