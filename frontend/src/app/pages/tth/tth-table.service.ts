import { Injectable } from '@angular/core';
import { TableService } from '../../core/components/table/table.service';
import { Page } from '../../core/model/page';
import {
  copyOverviewDetailFilterConfig,
  OverviewDetailTableFilterConfigType,
} from './overview-detail/overview-detail-table-filter-config';

@Injectable()
export class TthTableService extends TableService {
  private _activeTabPage?: Page;
  private _overviewDetailFilterConfig: OverviewDetailTableFilterConfigType =
    copyOverviewDetailFilterConfig();

  get overviewDetailFilterConfig() {
    return this._overviewDetailFilterConfig;
  }

  set activeTabPage(page: Page) {
    if (this._activeTabPage !== page) {
      this.resetTableSettings();
      this._overviewDetailFilterConfig = copyOverviewDetailFilterConfig();
      this._activeTabPage = page;
    }
  }
}
