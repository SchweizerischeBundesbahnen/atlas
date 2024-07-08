import {TableFilter} from './table-filter';
import {MatChipInputEvent} from "@angular/material/chips";
import {FormControl, ValidatorFn} from "@angular/forms";

export class TableFilterSingleSearch extends TableFilter<string | undefined> {
  protected activeSearch: string | undefined;
  formControl : FormControl;
  label: string;

  constructor(row: number, label:string, elementWidthCssClass: string, validator?: ValidatorFn) {
    super(row, elementWidthCssClass);
    this.label = label;
    this.formControl = new FormControl();
    if (validator) {
      this.formControl.addValidators(validator);
    }
  }

  setActiveSearch(value?: string): void {
    this.activeSearch = value;
    this.formControl.setValue(value);
  }

  getActiveSearch(): string | undefined {
    return this.activeSearch;
  }

  addSearchFromChipInputEvent(event: MatChipInputEvent): void {
    if (this.formControl.valid) {
      const value = (event.value || '').trim();
      if (value) {
        this.activeSearch = value;
      }
      event.chipInput.clear();
      this.formControl.reset();
    }
  }

}
