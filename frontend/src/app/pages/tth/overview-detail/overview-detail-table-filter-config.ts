import {
  FilterType,
  TableFilterChip,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
  TableFilterSearchType,
} from '../../../core/components/table-filter/table-filter-config';
import { StatementStatus, TimetableFieldNumber, TransportCompany } from '../../../api';

export const OverviewDetailTableFilterConfig: [
  [TableFilterChip],
  [
    TableFilterMultiSelect<StatementStatus>,
    TableFilterSearchSelect<TransportCompany>,
    TableFilterSearchSelect<TimetableFieldNumber>
  ]
] = [
  [
    {
      filterType: FilterType.CHIP_SEARCH,
      elementWidthCssClass: 'col-6',
      activeSearch: [],
      disabled: false,
    },
  ],
  [
    {
      filterType: FilterType.MULTI_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: [],
      labelTranslationKey: 'COMMON.STATUS',
      typeTranslationKeyPrefix: 'TTH.STATEMENT_STATUS.',
      selectOptions: Object.values(StatementStatus),
      disabled: false,
    },
    {
      filterType: FilterType.SEARCH_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: {} as TransportCompany,
      searchType: TableFilterSearchType.TRANSPORT_COMPANY,
      disabled: false,
    },
    {
      filterType: FilterType.SEARCH_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: {} as TimetableFieldNumber,
      searchType: TableFilterSearchType.TIMETABLE_FIELD_NUMBER,
      disabled: false,
    },
  ],
];
