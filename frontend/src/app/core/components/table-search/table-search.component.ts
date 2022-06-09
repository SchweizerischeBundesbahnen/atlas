import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { MatChipInputEvent } from '@angular/material/chips';
import { statusChoice, TableSearch } from './table-search';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { DATE_PATTERN, MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormControl, ValidationErrors } from '@angular/forms';
import { Status } from '../../../api';
import moment from 'moment/moment';
import { ValidationService } from '../../validation/validation.service';

@Component({
  selector: 'app-table-search',
  templateUrl: './table-search.component.html',
  styleUrls: ['./table-search.component.scss'],
})
export class TableSearchComponent {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Input() additionalFieldTemplate!: TemplateRef<any>;
  @Output() searchEvent: EventEmitter<TableSearch> = new EventEmitter<TableSearch>();
  @ViewChild('validOnInput') validOnInput!: ElementRef;

  readonly STATUS_OPTIONS = Object.values(Status);
  searchStrings: string[] = [];
  searchDate?: Date;
  activeStatuses: statusChoice = [];
  activeSearch: TableSearch = {
    searchCriteria: this.searchStrings,
    validOn: this.searchDate,
    statusChoices: this.activeStatuses,
  };

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

  statusSelectionChange(): void {
    this.emitSearch();
  }

  addSearch(event: MatChipInputEvent): void {
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

  removeSearch(search: string): void {
    const index = this.searchStrings.indexOf(search);
    if (index >= 0) {
      this.searchStrings.splice(index, 1);
    }
    this.emitSearch();
  }

  onDateChanged(event: MatDatepickerInputEvent<Date>): void {
    if (this.dateControl.invalid) return;
    this.searchDate = event.value ? moment(event.value).toDate() : undefined;
    this.emitSearch();
  }

  private emitSearch(): void {
    this.activeSearch.searchCriteria = this.searchStrings;
    this.activeSearch.validOn = this.searchDate;
    this.activeSearch.statusChoices = this.activeStatuses;
    this.searchEvent.emit(this.activeSearch);
  }
}
