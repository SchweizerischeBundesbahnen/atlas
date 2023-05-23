import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import {
  TableFilterChip,
  TableFilterConfig,
  TableFilterDateSelect,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
} from './table-filter-config';
import { Moment } from 'moment/moment';

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.scss'],
})
export class TableFilterComponent<TFilterConfig> {
  @Input() filterConfigurations: TableFilterConfig<TFilterConfig>[][] = [];
  @Output() searchEvent: EventEmitter<void> = new EventEmitter();

  public readonly TableFilterChipClass = TableFilterChip;
  public readonly TableFilterSearchSelectClass = TableFilterSearchSelect;
  public readonly TableFilterMultiSelectClass = TableFilterMultiSelect;
  public readonly TableFilterDateSelectClass = TableFilterDateSelect;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  handleDateChange(dateSelect: TableFilterDateSelect, value: Moment | null): void {
    if (dateSelect.formControl.invalid) {
      return;
    }
    dateSelect.setActiveSearch(value);
    this.emitSearch();
  }

  emitSearch(): void {
    this.searchEvent.emit();
  }
}
