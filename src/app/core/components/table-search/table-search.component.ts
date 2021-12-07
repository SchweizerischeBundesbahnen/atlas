import { Component, EventEmitter, Output } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { TableSearch } from './table-search';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import moment from 'moment/moment';

@Component({
  selector: 'app-table-search',
  templateUrl: './table-search.component.html',
  styleUrls: ['./table-search.component.scss'],
})
export class TableSearchComponent {
  @Output() searchEvent: EventEmitter<TableSearch> = new EventEmitter<TableSearch>();

  readonly separatorKeyCodes = [ENTER, COMMA] as const;
  searchStrings: string[] = [];
  searchDate?: Date;

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      this.searchStrings.push(value);
    }
    // Clear the input value
    event.chipInput!.clear();
    this.emitSearch({
      searchCriteria: this.searchStrings,
      validOn: this.searchDate,
    });
  }

  onDateChanged(event: MatDatepickerInputEvent<Date>): void {
    this.searchDate = moment(event.value).toDate();
    this.searchEvent.emit({
      searchCriteria: this.searchStrings,
      validOn: this.searchDate,
    });
  }

  private emitSearch(search: TableSearch): void {
    this.searchEvent.emit(search);
  }

  // TODO: check multiple of same value
  remove(search: string): void {
    const index = this.searchStrings.indexOf(search);
    if (index >= 0) {
      this.searchStrings.splice(index, 1);
    }
    this.emitSearch({
      searchCriteria: this.searchStrings,
      validOn: this.searchDate,
    });
  }
}
