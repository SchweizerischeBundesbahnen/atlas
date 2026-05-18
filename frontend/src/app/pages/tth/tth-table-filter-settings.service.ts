import { StatementStatus, TimetableFieldNumber, TransportCompany } from '../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { TableFilterMultiSelect } from '../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterSearchSelect } from '../../core/components/table-filter/config/table-filter-search-select';
import { TableFilterChip } from '../../core/components/table-filter/config/table-filter-chip';
import { TableFilterSearchType } from '../../core/components/table-filter/config/table-filter-search-type';
import { DossierStatus } from '../../api/model/dossierStatus';

export class TthTableFilterSettingsService {
  static createSettings() {
    return {
      chipSearch: new TableFilterChip(0, 'col-6'),
      multiSelectStatementStatus: new TableFilterMultiSelect(
        'TTH.STATEMENT_STATUS.',
        'COMMON.STATUS',
        Object.values(StatementStatus),
        1,
        'filter-width',
        []
      ),
      searchSelectTU: new TableFilterSearchSelect<TransportCompany[]>(
        TableFilterSearchType.TRANSPORT_COMPANY,
        1,
        'filter-width',
        new FormGroup({
          transportCompany: new FormControl([]),
        })
      ),
      searchSelectTTFN: new TableFilterSearchSelect<TimetableFieldNumber>(
        TableFilterSearchType.TIMETABLE_FIELD_NUMBER,
        1,
        'filter-width',
        new FormGroup({
          ttfnid: new FormControl(),
        })
      ),
    };
  }

  static createDossierSettings() {
    return {
      chipSearch: new TableFilterChip(0, 'col-6'),
      multiSelectDossierStatus: new TableFilterMultiSelect(
        'TTH.DOSSIER.DOSSIER_STATUS.',
        'COMMON.STATUS',
        Object.values(DossierStatus),
        1,
        'filter-width-single',
        ['ADDED', 'DOSSIER_BO_CHECK', 'DOSSIER_CANTON_CHECK', 'ACCEPTED', 'REJECTED', 'MOVED']
      ),
    };
  }
}
