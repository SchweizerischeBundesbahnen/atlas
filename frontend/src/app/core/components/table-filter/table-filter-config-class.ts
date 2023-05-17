import { FormControl, FormGroup } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Moment } from 'moment/moment';

export const TableFilterSearchType = {
  BUSINESS_ORGANISATION: 'BUSINESS_ORGANISATION' as TableFilterSearchType,
  TIMETABLE_FIELD_NUMBER: 'TIMETABLE_FIELD_NUMBER' as TableFilterSearchType,
  TRANSPORT_COMPANY: 'TRANSPORT_COMPANY' as TableFilterSearchType,
};

export type TableFilterSearchType =
  | 'BUSINESS_ORGANISATION'
  | 'TIMETABLE_FIELD_NUMBER'
  | 'TRANSPORT_COMPANY';

export abstract class TableFilterConfigClass<T> {
  elementWidthCssClass: string;

  constructor(elementWidthCssClass: string) {
    this.elementWidthCssClass = elementWidthCssClass;
  }

  protected abstract activeSearch: T;
  abstract getActiveSearch(): T;
}

export class TableFilterSearchSelectClass<T> extends TableFilterConfigClass<T | undefined> {
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
    console.log(value);
    this.activeSearch = value;
  }

  getActiveSearch(): T | undefined {
    return this.activeSearch;
  }
}

export class TableFilterMultiSelectClass<T> extends TableFilterConfigClass<T[]> {
  typeTranslationKeyPrefix: string;
  labelTranslationKey: string;
  selectOptions: T[];
  disabled?: boolean;

  protected activeSearch: T[];

  constructor(
    typeTranslationKeyPrefix: string,
    labelTranslationKey: string,
    selectOptions: T[],
    elementWidthCssClass: string,
    activeSearch?: T[]
  ) {
    super(elementWidthCssClass);
    this.typeTranslationKeyPrefix = typeTranslationKeyPrefix;
    this.labelTranslationKey = labelTranslationKey;
    this.selectOptions = selectOptions;
    this.activeSearch = activeSearch ?? [];
  }

  setActiveSearch(value: T[]): void {
    this.activeSearch = value;
  }

  getActiveSearch(): T[] {
    return this.activeSearch;
  }
}

export class TableFilterDateSelectClass extends TableFilterConfigClass<Date | undefined> {
  formControl: FormControl<Date | null>;

  protected activeSearch: Date | undefined;

  constructor(elementWidthCssClass: string);
  constructor(elementWidthCssClass: string, formControl?: FormControl<Date | null>) {
    super(elementWidthCssClass);
    this.formControl = formControl ?? new FormControl();
  }

  setActiveSearch(value: Moment | null): void {
    if (this.formControl.invalid) {
      return;
    }
    this.activeSearch = value?.toDate();
  }

  getActiveSearch(): Date | undefined {
    return this.activeSearch;
  }
}

export class TableFilterChipClass extends TableFilterConfigClass<string[]> {
  disabled?: boolean;

  protected activeSearch: string[] = [];

  getActiveSearch(): string[] {
    return this.activeSearch;
  }

  addSearchFromString(value: string): void {
    this.activeSearch.push(value);
  }

  addSearchFromChipInputEvent(event: MatChipInputEvent): void {
    console.log(event);
    const value = (event.value || '').trim();
    if (this.activeSearch.indexOf(value) !== -1) {
      event.chipInput!.clear();
      return;
    }
    if (value) {
      this.activeSearch.push(value);
    }
    // Clear the input value
    event.chipInput!.clear();
  }

  removeSearch(search: string): void {
    const index = this.activeSearch.indexOf(search);
    if (index >= 0) {
      this.activeSearch.splice(index, 1);
    }
  }
}
