import { Injectable } from '@angular/core';
import { TableSettings } from './table-settings';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { Pages } from '../../../pages/pages';

@Injectable({
  providedIn: 'root',
})
export class TableSettingsService {
  private tableKeys = [
    Pages.TTFN.path,
    Pages.LINES.path,
    Pages.SUBLINES.path,
    Pages.BUSINESS_ORGANISATIONS.path,
    Pages.TRANSPORT_COMPANIES.path,
  ];
  private navigationStartUrl!: string;

  constructor(public router: Router) {
    this.router.events.subscribe((e) => {
      if (e instanceof NavigationStart) {
        this.navigationStartUrl = this.router.url;
      }
      if (e instanceof NavigationEnd) {
        this.handleNavigationEnd(e);
      }
    });
  }

  private handleNavigationEnd(e: NavigationEnd) {
    if (
      this.navigationStartUrl &&
      (!this.isOverviewRoute() || this.navigationStartUrl !== e.urlAfterRedirects)
    ) {
      const currentTable = this.tableKeys.find((tableKey) =>
        e.urlAfterRedirects.includes('/' + tableKey)
      );
      this.deleteAllTableSettingsBut(currentTable!);
    } else {
      this.deleteAllTableSettings();
    }
  }

  private isOverviewRoute(): boolean {
    const overview = this.tableKeys.find((tableKey) =>
      this.navigationStartUrl.endsWith('/' + tableKey)
    );
    return !!overview;
  }

  getTableSettings(key: string): TableSettings | undefined {
    const storedSettings = sessionStorage.getItem(key);
    if (storedSettings) {
      return JSON.parse(storedSettings);
    }
    return undefined;
  }

  storeTableSettings(key: string, tableSettings: TableSettings) {
    sessionStorage.setItem(key, JSON.stringify(tableSettings));
  }

  private deleteAllTableSettings() {
    this.tableKeys.forEach((tableKey) => TableSettingsService.deleteTableSettings(tableKey));
  }

  private deleteAllTableSettingsBut(key: string) {
    this.tableKeys.forEach((tableKey) => {
      if (tableKey !== key) {
        TableSettingsService.deleteTableSettings(tableKey);
      }
    });
  }

  private static deleteTableSettings(key: string) {
    sessionStorage.removeItem(key);
  }
}
