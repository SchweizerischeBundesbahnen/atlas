import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormControl, FormGroup } from '@angular/forms';
import moment from 'moment';
import {
  isChipSearch,
  isDateSelect,
  isMultiSelect,
  isSearchSelect,
  TableFilterChip,
  TableFilterConfig,
  TableFilterDateSelect,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
} from './table-filter-config';
import { MatSelectChange } from '@angular/material/select';
import { TypeGuard } from './filter-type-guard.pipe';
import { TimetableFieldNumber } from '../../../api';

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.scss'],
})
export class TableFilterComponent<TFilterConfig> implements OnInit {
  @Input() filterConfigurations: TableFilterConfig<TFilterConfig>[][] = [];
  @Output() searchEvent: EventEmitter<void> = new EventEmitter();

  readonly isDateSelect: TypeGuard<TableFilterConfig<TFilterConfig>, TableFilterDateSelect> =
    isDateSelect;
  readonly isSearchSelect: TypeGuard<
    TableFilterConfig<TFilterConfig>,
    TableFilterSearchSelect<TFilterConfig>
  > = isSearchSelect;
  readonly isMultiSelect: TypeGuard<
    TableFilterConfig<TFilterConfig>,
    TableFilterMultiSelect<TFilterConfig>
  > = isMultiSelect;
  readonly isChipSearch: TypeGuard<TableFilterConfig<TFilterConfig>, TableFilterChip> =
    isChipSearch;

  searchForm: FormGroup = new FormGroup({});

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  ngOnInit() {
    let businessOrganisationValue;
    let ttfnidValue;
    let transportCompanyValue;

    for (const row of this.filterConfigurations) {
      for (const filter of row) {
        if ('searchType' in filter) {
          if (filter.searchType === 'BUSINESS_ORGANISATION') {
            businessOrganisationValue = filter.activeSearch;
          } else if (filter.searchType === 'TIMETABLE_FIELD_NUMBER') {
            ttfnidValue = (filter.activeSearch as TimetableFieldNumber | undefined)?.ttfnid;
          } else if (filter.searchType === 'TRANSPORT_COMPANY') {
            transportCompanyValue = filter.activeSearch;
          }
        }
      }
    }

    this.searchForm = new FormGroup({
      businessOrganisation: new FormControl(businessOrganisationValue),
      ttfnid: new FormControl(ttfnidValue),
      transportCompany: new FormControl(transportCompanyValue),
    });
  }

  addSearch(event: MatChipInputEvent, rowIndex: number, filterIndex: number): void {
    const value = (event.value || '').trim();
    const activeSearchStrings: string[] = (
      this.filterConfigurations[rowIndex][filterIndex] as TableFilterChip
    ).activeSearch;
    if (activeSearchStrings.indexOf(value) !== -1) {
      event.chipInput!.clear();
      return;
    }
    if (value) {
      activeSearchStrings.push(value);
    }
    // Clear the input value
    event.chipInput!.clear();
    this.emitSearch();
  }

  removeSearch(search: string, rowIndex: number, filterIndex: number): void {
    const activeSearchStrings: string[] = (
      this.filterConfigurations[rowIndex][filterIndex] as TableFilterChip
    ).activeSearch;
    const index = activeSearchStrings.indexOf(search);
    if (index >= 0) {
      activeSearchStrings.splice(index, 1);
    }
    this.emitSearch();
  }

  onDateChanged(event: MatDatepickerInputEvent<Date>, rowIndex: number, filterIndex: number): void {
    if (
      (this.filterConfigurations[rowIndex][filterIndex] as TableFilterDateSelect).formControl
        .invalid
    ) {
      return;
    }

    (this.filterConfigurations[rowIndex][filterIndex] as TableFilterDateSelect).activeSearch =
      event.value ? moment(event.value).toDate() : undefined;

    this.emitSearch();
  }

  multiSelectChanged(changeEvent: MatSelectChange, rowIndex: number, filterIndex: number) {
    (
      this.filterConfigurations[rowIndex][filterIndex] as TableFilterMultiSelect<TFilterConfig>
    ).activeSearch = changeEvent.value as TFilterConfig[];

    this.emitSearch();
  }

  multiSelectSearchChanged(changeEvent: unknown, rowIndex: number, filterIndex: number) {
    (
      this.filterConfigurations[rowIndex][filterIndex] as TableFilterSearchSelect<TFilterConfig>
    ).activeSearch = changeEvent as TFilterConfig;

    this.emitSearch();
  }

  private emitSearch(): void {
    this.searchEvent.emit();
  }
}
