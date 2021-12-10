import { Component, EventEmitter, Output } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { TableSearch } from './table-search';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { DATE_PATTERN, MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormControl, ValidationErrors } from '@angular/forms';
import { Version } from '../../../api';
import moment from 'moment/moment';
import { ValidationService } from '../../validation/validation.service';

@Component({
  selector: 'app-table-search',
  templateUrl: './table-search.component.html',
  styleUrls: ['./table-search.component.scss'],
})
export class TableSearchComponent {
  @Output() searchEvent: EventEmitter<TableSearch> = new EventEmitter<TableSearch>();

  readonly separatorKeyCodes = [ENTER, COMMA] as const;
  readonly STATUS_OPTIONS = Object.values(Version.StatusEnum);
  searchStrings: string[] = [];
  searchDate?: Date;
  activeStatuses: string[] = [];

  dateControl = new FormControl();

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  constructor(private readonly validationService: ValidationService) {}

  getDateControlValidation() {
    return this.validationService.getValidation(this.dateControl.errors);
  }

  getMinOrMaxDateDisplay(validationErrors: ValidationErrors) {
    if (validationErrors.min) return validationErrors.min.format(DATE_PATTERN);
    if (validationErrors.max) return validationErrors.max.format(DATE_PATTERN);
  }

  checkboxChanged(checked: boolean, status: string): void {
    if (checked) {
      this.activeStatuses.push(status);
    } else {
      const index = this.activeStatuses.indexOf(status);
      this.activeStatuses.splice(index, 1);
    }
    this.emitSearch();
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (this.searchStrings.indexOf(value) !== -1) {
      event.chipInput!.clear();
      return;
    }
    if (value) {
      this.searchStrings.push(value);
    }
    // Clear the input value
    event.chipInput!.clear();
    this.emitSearch();
  }

  onDateChanged(event: MatDatepickerInputEvent<Date>): void {
    if (this.dateControl.invalid) return;
    this.searchDate = event.value ? moment(event.value).toDate() : undefined;
    this.emitSearch();
  }

  private emitSearch(): void {
    this.searchEvent.emit({
      searchCriteria: this.searchStrings.concat(this.activeStatuses),
      validOn: this.searchDate,
    });
  }

  remove(search: string): void {
    const index = this.searchStrings.indexOf(search);
    if (index >= 0) {
      this.searchStrings.splice(index, 1);
    }
    this.emitSearch();
  }
}
