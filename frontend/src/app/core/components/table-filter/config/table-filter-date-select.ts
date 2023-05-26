import { FormControl } from '@angular/forms';
import { Moment } from 'moment';
import { TableFilter } from './table-filter';

export class TableFilterDateSelect extends TableFilter<Date | undefined> {
  formControl: FormControl<Date | null>;

  protected activeSearch: Date | undefined;

  constructor(elementWidthCssClass: string);
  constructor(elementWidthCssClass: string, formControl?: FormControl<Date | null>) {
    super(elementWidthCssClass);
    this.formControl = formControl ?? new FormControl();
  }

  setActiveSearch(value: Moment | null): void {
    this.activeSearch = value?.toDate();
  }

  getActiveSearch(): Date | undefined {
    return this.activeSearch;
  }
}
