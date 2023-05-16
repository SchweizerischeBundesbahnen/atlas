import {
  FilterType,
  TableFilterChip,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
  TableFilterSearchType,
} from '../../../core/components/table-filter/table-filter-config';
import { StatementStatus, TimetableFieldNumber, TransportCompany } from '../../../api';

export type OverviewDetailTableFilterConfigType = [
  [TableFilterChip],
  [
    TableFilterMultiSelect<StatementStatus>,
    TableFilterSearchSelect<TransportCompany>,
    TableFilterSearchSelect<TimetableFieldNumber>
  ]
];

export const OverviewDetailTableFilterConfig: OverviewDetailTableFilterConfigType = [
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

export function copyOverviewDetailFilterConfig(): OverviewDetailTableFilterConfigType {
  return [
    [
      {
        ...OverviewDetailTableFilterConfig[0][0],
      },
    ],
    [
      { ...OverviewDetailTableFilterConfig[1][0] },
      { ...OverviewDetailTableFilterConfig[1][1] },
      { ...OverviewDetailTableFilterConfig[1][2] },
    ],
  ];
}

export function disableFilters(tableFilterConfig: OverviewDetailTableFilterConfigType) {
  tableFilterConfig[0][0].disabled = true;
  tableFilterConfig[1][0].disabled = true;
  tableFilterConfig[1][1].disabled = true;
  tableFilterConfig[1][2].disabled = true;
}

export function enableFilters(tableFilterConfig: OverviewDetailTableFilterConfigType) {
  tableFilterConfig[0][0].disabled = false;
  tableFilterConfig[1][0].disabled = false;
  tableFilterConfig[1][1].disabled = false;
  tableFilterConfig[1][2].disabled = false;
}
