import { FormGroup } from '@angular/forms';
import { TableFilter } from './table-filter';
import { TableFilterSearchType } from './table-filter-search-type';

export class TableFilterSearchSelect<T> extends TableFilter<T | undefined> {
  searchType: TableFilterSearchType;
  disabled?: boolean;
  formGroup?: FormGroup;

  protected activeSearch: T | undefined;

  constructor(
    searchType: TableFilterSearchType,
    elementWidthCssClass: string,
    formGroup?: FormGroup
  ) {
    super(elementWidthCssClass);
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
