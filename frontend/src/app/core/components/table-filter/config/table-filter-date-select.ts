import { FormControl } from '@angular/forms';
import { Moment } from 'moment';
import { TableFilter } from './table-filter';

export class TableFilterDateSelect extends TableFilter<Date | undefined> {
  protected activeSearch: Date | undefined;
  formControl: FormControl<Date | null>;

  constructor(row: number, elementWidthCssClass: string);
  constructor(
    row: number,
    elementWidthCssClass: string,
    formControl?: FormControl<Date | null>
  ) {
    super(row, elementWidthCssClass);
    this.formControl = formControl ?? new FormControl();
  }

  setActiveSearch(value: Moment | null): void {
    this.activeSearch = value?.toDate();
  }

  getActiveSearch(): Date | undefined {
    return this.activeSearch;
  }
}
