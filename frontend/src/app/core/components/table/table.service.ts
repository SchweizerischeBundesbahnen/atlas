import {Injectable} from '@angular/core';
import {SortDirection} from '@angular/material/sort';
import {TableFilterConfig} from './table-filter-config';
import {Page} from '../../model/page';
import {TableFilters} from './table-filters-type';

@Injectable({ providedIn: 'root' })
export class TableService {
  private _pageSize = 10;

  private _filterConfig!: TableFilterConfig;

  initializeFilterConfig(settings: TableFilters, page: Page) {
    this.filterConfig = new TableFilterConfig(settings, page);
    return this.filterConfig.settingsInRowRepresentation;
  }

  set filterConfig(tableFilterConfig: TableFilterConfig) {
    console.log(this._filterConfig);
    console.log(tableFilterConfig);
    // Object.keys(this._filterConfig?.filters);
    // delete this._filterConfig?.filters['filterByNoDecision']
    // if (this._filterConfig != undefined && this._filterConfig.filters)
    if (this._filterConfig != undefined) {
      const result : string[] =  this.deleteOrphanFilters(tableFilterConfig, this._filterConfig);
      result.forEach(key => {
        delete this._filterConfig.filters[key];
      });
    }
    if (tableFilterConfig.page != this._filterConfig?.page) {
      this._filterConfig = tableFilterConfig;
      this.resetTableSettings();
    }
  }

  private deleteOrphanFilters(tableFilterConfig: TableFilterConfig, oldTableFilterConfig: TableFilterConfig) : string[] {

    const keysOld = Object.keys(oldTableFilterConfig.filters);
    const keysNew = Object.keys(tableFilterConfig.filters);

    console.log(keysOld);
    console.log(keysNew);


  // Create a new list for keys that exist in keysOld but not in keysNew
    const result: string[] = keysOld.filter(key => !keysNew.includes(key));

    console.log(result);
    return result;
  }

  get filterConfig() {
    return this._filterConfig;
  }

  get filter() {
    return this._filterConfig.filters;
  }

  get pageSize(): number {
    return this._pageSize;
  }

  set pageSize(pageSize: number) {
    this._pageSize = pageSize;
  }

  private _pageIndex = 0;

  get pageIndex(): number {
    return this._pageIndex;
  }

  set pageIndex(pageIndex: number) {
    this._pageIndex = pageIndex;
  }

  // sort
  private _sortActive = '';

  get sortActive(): string {
    return this._sortActive;
  }

  set sortActive(activeSort: string) {
    this._sortActive = activeSort;
  }

  private _sortDirection: SortDirection = 'asc';

  get sortDirection(): SortDirection {
    return this._sortDirection;
  }

  set sortDirection(direction: SortDirection) {
    this._sortDirection = direction;
  }

  get sortString(): string | undefined {
    if (this._sortActive.length === 0 || this._sortDirection.length === 0) {
      return undefined;
    } else {
      return `${this._sortActive},${this._sortDirection}`;
    }
  }

  protected resetTableSettings(): void {
    this._pageSize = 10;
    this._pageIndex = 0;
    this._sortActive = '';
    this._sortDirection = 'asc';
  }
}
