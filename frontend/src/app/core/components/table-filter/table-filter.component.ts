import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import {
  TableFilterChipClass,
  TableFilterConfigClass,
  TableFilterDateSelectClass,
  TableFilterMultiSelectClass,
  TableFilterSearchSelectClass,
} from './table-filter-config-class';

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.scss'],
})
export class TableFilterComponent<TFilterConfig> {
  @Input() filterConfigurations: TableFilterConfigClass<TFilterConfig>[][] = [];
  @Output() searchEvent: EventEmitter<void> = new EventEmitter();

  public readonly TableFilterChipClass = TableFilterChipClass;
  public readonly TableFilterSearchSelectClass = TableFilterSearchSelectClass;
  public readonly TableFilterMultiSelectClass = TableFilterMultiSelectClass;
  public readonly TableFilterDateSelectClass = TableFilterDateSelectClass;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  emitSearch(): void {
    this.searchEvent.emit();
  }
}
