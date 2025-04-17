import {
  StatementStatus,
  TimetableFieldNumber,
  TransportCompany,
} from '../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { TableFilterMultiSelect } from '../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterSearchSelect } from '../../core/components/table-filter/config/table-filter-search-select';
import { TableFilterChip } from '../../core/components/table-filter/config/table-filter-chip';
import { TableFilterSearchType } from '../../core/components/table-filter/config/table-filter-search-type';

export class TthTableFilterSettingsService {
  static createSettings() {
    return {
      chipSearch: new TableFilterChip(0, 'col-6'),
      multiSelectStatementStatus: new TableFilterMultiSelect(
        'TTH.STATEMENT_STATUS.',
        'COMMON.STATUS',
        Object.values(StatementStatus),
        1,
        'col-3',
        []
      ),
      searchSelectTU: new TableFilterSearchSelect<TransportCompany[]>(
        TableFilterSearchType.TRANSPORT_COMPANY,
        1,
        'col-3',
        new FormGroup({
          transportCompany: new FormControl([]),
        })
      ),
      searchSelectTTFN: new TableFilterSearchSelect<TimetableFieldNumber>(
        TableFilterSearchType.TIMETABLE_FIELD_NUMBER,
        1,
        'col-3',
        new FormGroup({
          ttfnid: new FormControl(),
        })
      ),
    };
  }
}
