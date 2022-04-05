import { Injectable, Injector } from '@angular/core';
import { TableSettings } from './table-settings';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Pages } from '../../../pages/pages';

@Injectable({
  providedIn: 'root',
})
export class TableSettingsService {
  private tableKeys = [Pages.TTFN.path, Pages.LINES.path, Pages.SUBLINES.path];

  constructor(private router: Router, private injector: Injector) {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((e) => {
      if (e instanceof NavigationEnd) {
        const currentTable = this.tableKeys.find((tableKey) =>
          e.urlAfterRedirects.includes('/' + tableKey)
        );
        this.deleteAllTableSettingsBut(currentTable!);
      }
    });
  }

  getTableSettings(key: string): TableSettings | undefined {
    const storedSettings = localStorage.getItem(key);
    if (storedSettings) {
      return JSON.parse(storedSettings);
    }
    return undefined;
  }

  storeTableSettings(key: string, tableSettings: TableSettings) {
    localStorage.setItem(key, JSON.stringify(tableSettings));
  }

  deleteAllTableSettingsBut(key: string) {
    this.tableKeys.forEach((tableKey) => {
      if (tableKey !== key) {
        TableSettingsService.deleteTableSettings(tableKey);
      }
    });
  }

  private static deleteTableSettings(key: string) {
    localStorage.removeItem(key);
  }
}
