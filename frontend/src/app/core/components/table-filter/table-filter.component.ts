import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MAX_DATE, MIN_DATE} from '../../date/date.service';
import {Moment} from 'moment/moment';
import {TableFilterChip} from './config/table-filter-chip';
import {TableFilterSearchSelect} from './config/table-filter-search-select';
import {TableFilterMultiSelect} from './config/table-filter-multiselect';
import {TableFilterDateSelect} from './config/table-filter-date-select';
import {TableFilter} from './config/table-filter';
import {TableFilterSingleSearch} from "./config/table-filter-single-search";
import {TableFilterBoolean} from "./config/table-filter-boolean";

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.scss'],
})
export class TableFilterComponent<TFilterConfig> {
  @Input() filterConfigurations: TableFilter<TFilterConfig>[][] = [];
  @Input() tableFilterBooleanIsVisible = true;
  @Output() searchEvent: EventEmitter<void> = new EventEmitter();

  public readonly TableFilterChipClass = TableFilterChip;
  public readonly TableFilterSearchSelectClass = TableFilterSearchSelect;
  public readonly TableFilterMultiSelectClass = TableFilterMultiSelect;
  public readonly TableFilterDateSelectClass = TableFilterDateSelect;
  public readonly TableFilterSingleSearchClass = TableFilterSingleSearch;
  public readonly TableFilterBooleanClass = TableFilterBoolean;

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
