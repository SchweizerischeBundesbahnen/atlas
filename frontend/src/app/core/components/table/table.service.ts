import { Injectable } from '@angular/core';
import { SortDirection } from '@angular/material/sort';

@Injectable()
export class TableService {
  private _pageSize = 10;

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
