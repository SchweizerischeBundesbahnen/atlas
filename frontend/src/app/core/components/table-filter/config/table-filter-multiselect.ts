import { TableFilter } from './table-filter';

export class TableFilterMultiSelect<T> extends TableFilter<T[]> {
  typeTranslationKeyPrefix: string;
  labelTranslationKey: string;
  selectOptions: T[];
  disabled?: boolean;

  activeSearch: T[];

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
