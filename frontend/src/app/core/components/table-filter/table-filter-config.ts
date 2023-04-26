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

export const TableFilterSearchType = {
  BUSINESS_ORGANISATION: 'BUSINESS_ORGANISATION' as TableFilterSearchType,
  TIMETABLE_FIELD_NUMBER: 'TIMETABLE_FIELD_NUMBER' as TableFilterSearchType,
  TRANSPORT_COMPANY: 'TRANSPORT_COMPANY' as TableFilterSearchType,
};
export type TableFilterSearchType =
  | 'BUSINESS_ORGANISATION'
  | 'TIMETABLE_FIELD_NUMBER'
  | 'TRANSPORT_COMPANY';

export type TableFilterSearchSelect<T> = {
  filterType: FilterType.SEARCH_SELECT;
  elementWidthCssClass: string;
  activeSearch: T | undefined;
  searchType: TableFilterSearchType;
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

export function isDateSelect<TFilterConfig>(
  filterType: TableFilterConfig<TFilterConfig>
): filterType is TableFilterDateSelect {
  return filterType.filterType === FilterType.VALID_ON_SELECT;
}

export function isSearchSelect<TFilterConfig>(
  filterType: TableFilterConfig<TFilterConfig>
): filterType is TableFilterSearchSelect<TFilterConfig> {
  return filterType.filterType === FilterType.SEARCH_SELECT;
}

export function isMultiSelect<TFilterConfig>(
  filterType: TableFilterConfig<TFilterConfig>
): filterType is TableFilterMultiSelect<TFilterConfig> {
  return filterType.filterType === FilterType.MULTI_SELECT;
}

export function isChipSearch<TFilterConfig>(
  filterType: TableFilterConfig<TFilterConfig>
): filterType is TableFilterChip {
  return filterType.filterType === FilterType.CHIP_SEARCH;
}

export function getActiveSearch<
  ExpectedType extends TFilterConfig | TFilterConfig[] | undefined,
  TFilterConfig
>(
  filterType: TableFilterMultiSelect<TFilterConfig> | TableFilterSearchSelect<TFilterConfig>
): ExpectedType {
  return filterType.activeSearch as ExpectedType;
}

export function getActiveMultiSearch<TFilterConfig>(
  filterType: TableFilterMultiSelect<TFilterConfig> | TableFilterSearchSelect<TFilterConfig>
): Array<TFilterConfig> {
  if (!filterType.activeSearch) {
    return [];
  }
  return filterType.activeSearch as Array<TFilterConfig>;
}

export function getActiveSearchDate(filterType: TableFilterDateSelect): Date | undefined {
  return filterType.activeSearch;
}

export function getActiveSearchForChip(filterType: TableFilterChip) {
  return filterType.activeSearch;
}
