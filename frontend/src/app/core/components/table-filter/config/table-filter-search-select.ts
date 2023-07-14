import { FormGroup } from '@angular/forms';
import { TableFilter } from './table-filter';
import { TableFilterSearchType } from './table-filter-search-type';

export class TableFilterSearchSelect<T> extends TableFilter<T | undefined> {
  protected activeSearch: T | undefined;
  searchType: TableFilterSearchType;
  formGroup?: FormGroup;

  constructor(
    searchType: TableFilterSearchType,
    row: number,
    elementWidthCssClass: string,
    formGroup?: FormGroup
  ) {
    super(row, elementWidthCssClass);
    this.searchType = searchType;
    this.formGroup = formGroup;
  }

  setActiveSearch(value: T | undefined): void {
    this.activeSearch = value;
  }

  getActiveSearch(): T | undefined {
    return this.activeSearch;
  }
}
