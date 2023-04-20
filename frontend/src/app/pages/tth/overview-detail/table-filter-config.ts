import {
  FilterType,
  TableFilterChip,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
} from '../../../core/components/table-filter/table-filter-config';
import { StatementStatus, TimetableFieldNumber, TransportCompany } from '../../../api';

export const tableFilterConfig: [
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
    },
  ],
  [
    {
      filterType: FilterType.MULTI_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: [],
      labelTranslationKey: 'LIDI.TYPE',
      typeTranslationKeyPrefix: 'LIDI.LINE.TYPES.',
      selectOptions: Object.values(StatementStatus),
    },
    {
      filterType: FilterType.SEARCH_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: {} as TransportCompany,
    },
    {
      filterType: FilterType.SEARCH_SELECT,
      elementWidthCssClass: 'col-3',
      activeSearch: {} as TimetableFieldNumber,
    },
  ],
];
