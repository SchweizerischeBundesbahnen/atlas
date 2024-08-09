import {TableFilter} from './table-filter';
import {FormControl} from '@angular/forms';

export class TableFilterBoolean extends TableFilter<boolean> {
  protected activeSearch: boolean;
  formControl: FormControl;
  fieldLabel: string;

  constructor(row: number, elementWidthCssClass: string, fieldLabel: string, initialValue: boolean = false) {
    super(row, elementWidthCssClass);
    this.fieldLabel = fieldLabel;
    this.activeSearch = initialValue;
    this.formControl = new FormControl(initialValue);
  }

  setActiveSearch(value: boolean): void {
    this.activeSearch = value;
    this.formControl.setValue(value);
  }

  getActiveSearch(): boolean {
    return this.activeSearch;
  }
}
