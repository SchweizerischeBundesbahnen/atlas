import { FormControl } from '@angular/forms';

export enum FilterType {
  SEARCH_SELECT,
  MULTI_SELECT,
  VALID_ON_SELECT,
  CHIP_SEARCH,
}

export type TableFilterConfig<TFilterConfig> =
  | TableFilterSearchSelect<TFilterConfig>
  | TableFilterMultiSelect<TFilterConfig>
  | TableFilterDateSelect
  | TableFilterChip;

export type TableFilterSearchSelect<T> = {
  filterType: FilterType.SEARCH_SELECT;
  elementWidthCssClass: string;
  activeSearch: T;
};

export type TableFilterMultiSelect<T> = {
  filterType: FilterType.MULTI_SELECT;
  elementWidthCssClass: string;
  activeSearch: T[];
  typeTranslationKeyPrefix: string;
  labelTranslationKey: string;
  selectOptions: T[];
};

export type TableFilterDateSelect = {
  filterType: FilterType.VALID_ON_SELECT;
  elementWidthCssClass: string;
  activeSearch: Date | undefined;
  formControl: FormControl<Date | null>;
};

export type TableFilterChip = {
  filterType: FilterType.CHIP_SEARCH;
  elementWidthCssClass: string;
  activeSearch: string[];
};

export function getActiveSearch<
  ExpectedType extends TFilterConfig | TFilterConfig[] | undefined,
  TFilterConfig
>(
  filterType: TableFilterMultiSelect<TFilterConfig> | TableFilterSearchSelect<TFilterConfig>
): ExpectedType {
  return filterType.activeSearch as ExpectedType;
}

export function getActiveSearchDate(filterType: TableFilterDateSelect): Date | undefined {
  return filterType.activeSearch;
}

export function getActiveSearchForChip(filterType: TableFilterChip) {
  return filterType.activeSearch;
}
