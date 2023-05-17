import { Injectable } from '@angular/core';
import { TableService } from '../../core/components/table/table.service';
import { Page } from '../../core/model/page';
import {
  TableFilterChipClass,
  TableFilterConfigClass,
  TableFilterMultiSelectClass,
  TableFilterSearchSelectClass,
  TableFilterSearchType,
} from '../../core/components/table-filter/table-filter-config-class';
import { StatementStatus, TimetableFieldNumber, TransportCompany } from '../../api';
import { BehaviorSubject } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';

interface OverviewDetailFilterConfigInternal {
  chipSearch: TableFilterChipClass;
  multiSelectStatementStatus: TableFilterMultiSelectClass<StatementStatus>;
  searchSelectTU: TableFilterSearchSelectClass<TransportCompany[]>;
  searchSelectTTFN: TableFilterSearchSelectClass<TimetableFieldNumber>;
}

@Injectable()
export class TthTableService extends TableService {
  private _activeTabPage?: Page;
  private _overviewDetailFilterConfigInternal: OverviewDetailFilterConfigInternal =
    this.createTableFilterConfigInternal();
  private readonly _overviewDetailFilterConfig$: BehaviorSubject<
    TableFilterConfigClass<unknown>[][]
  > = new BehaviorSubject(this.getTableFilterConfig());

  get overviewDetailFilterConfigInternal() {
    return this._overviewDetailFilterConfigInternal;
  }

  get overviewDetailFilterConfig() {
    return this._overviewDetailFilterConfig$;
  }

  set activeTabPage(page: Page) {
    if (this._activeTabPage !== page) {
      this.resetTableSettings();
      this._overviewDetailFilterConfigInternal = this.createTableFilterConfigInternal();
      this._overviewDetailFilterConfig$.next(this.getTableFilterConfig());
      this._activeTabPage = page;
    }
  }

  private createTableFilterConfigInternal(): OverviewDetailFilterConfigInternal {
    return {
      chipSearch: new TableFilterChipClass('col-6'),
      multiSelectStatementStatus: new TableFilterMultiSelectClass(
        'TTH.STATEMENT_STATUS.',
        'COMMON.STATUS',
        Object.values(StatementStatus),
        'col-3',
        []
      ),
      searchSelectTU: new TableFilterSearchSelectClass<TransportCompany[]>(
        TableFilterSearchType.TRANSPORT_COMPANY,
        'col-3',
        new FormGroup({
          transportCompany: new FormControl(),
        })
      ),
      searchSelectTTFN: new TableFilterSearchSelectClass<TimetableFieldNumber>(
        TableFilterSearchType.TIMETABLE_FIELD_NUMBER,
        'col-3',
        new FormGroup({
          ttfnid: new FormControl(),
        })
      ),
    };
  }

  private getTableFilterConfig(): TableFilterConfigClass<unknown>[][] {
    return [
      [this._overviewDetailFilterConfigInternal.chipSearch],
      [
        this._overviewDetailFilterConfigInternal.multiSelectStatementStatus,
        this._overviewDetailFilterConfigInternal.searchSelectTU,
        this._overviewDetailFilterConfigInternal.searchSelectTTFN,
      ],
    ];
  }

  disableFilters(): void {
    this._overviewDetailFilterConfigInternal.chipSearch.disabled = true;
    this._overviewDetailFilterConfigInternal.multiSelectStatementStatus.disabled = true;
    this._overviewDetailFilterConfigInternal.searchSelectTU.disabled = true;
    this._overviewDetailFilterConfigInternal.searchSelectTTFN.disabled = true;
  }

  enableFilters(): void {
    this._overviewDetailFilterConfigInternal.chipSearch.disabled = false;
    this._overviewDetailFilterConfigInternal.multiSelectStatementStatus.disabled = false;
    this._overviewDetailFilterConfigInternal.searchSelectTU.disabled = false;
    this._overviewDetailFilterConfigInternal.searchSelectTTFN.disabled = false;
  }
}
