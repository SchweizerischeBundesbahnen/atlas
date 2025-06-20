import { MatChipInputEvent } from '@angular/material/chips';
import { TableFilter } from './table-filter';

export class TableFilterChip extends TableFilter<string[]> {
  activeSearch: string[] = [];
  fieldLabel = 'COMMON.SEARCH.SEARCH_CRITERIA';

  constructor(row: number, elementWidthCssClass: string, fieldLabel?: string) {
    super(row, elementWidthCssClass);
    if (fieldLabel) {
      this.fieldLabel = fieldLabel;
    }
  }

  getActiveSearch(): string[] {
    return this.activeSearch;
  }

  addSearchFromString(value: string): void {
    this.activeSearch.push(value);
  }

  addSearchFromChipInputEvent(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (this.activeSearch.indexOf(value) !== -1) {
      event.chipInput.clear();
      return;
    }
    if (value) {
      this.activeSearch.push(value);
    }
    // Clear the input value
    event.chipInput.clear();
  }

  removeSearch(search: string): void {
    const index = this.activeSearch.indexOf(search);
    if (index >= 0) {
      this.activeSearch.splice(index, 1);
    }
  }
}
