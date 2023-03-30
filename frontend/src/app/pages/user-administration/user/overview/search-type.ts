interface Search {
  value: SearchType;
  translationKey: string;
}

export type SearchType = 'USER' | 'FILTER' | 'FILTER_CANTON';

export const SearchTypes: Search[] = [
  {
    value: 'USER',
    translationKey: 'USER_ADMIN.TABLE_HEADER',
  },
  {
    value: 'FILTER',
    translationKey: 'USER_ADMIN.SEARCH_FOR_FILTER',
  },
  {
    value: 'FILTER_CANTON',
    translationKey: 'USER_ADMIN.SEARCH_FOR_FILTER_CANTON',
  },
];
