import { Injectable } from '@angular/core';
import { TableService } from '../../core/components/table/table.service';
import { Page } from '../../core/model/page';

@Injectable()
export class TthTableService extends TableService {
  private _activeTabPage?: Page;

  set activeTabPage(page: Page) {
    if (this._activeTabPage !== page) {
      this.resetTableSettings();
      this._activeTabPage = page;
    }
  }
}
