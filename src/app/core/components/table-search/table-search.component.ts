import { Component, EventEmitter, Output } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { TableSearch } from './table-search';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import moment from 'moment/moment';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-table-search',
  templateUrl: './table-search.component.html',
  styleUrls: ['./table-search.component.scss'],
})
export class TableSearchComponent {
  @Output() searchEvent: EventEmitter<TableSearch> = new EventEmitter<TableSearch>();

  readonly separatorKeyCodes = [ENTER, COMMA] as const;
  readonly statusTranslateMapping = {
    ACTIVE: ['aktiv', 'actif', 'attivo'],
    INACTIVE: ['inaktiv', 'inactif', 'inaattivo'],
    NEEDS_REVIEW: ['benötigt prüfung', 'nécessite un examen', 'richiede esame'],
    IN_REVIEW: ['in prüfung', "en cours d'examen", 'in esame'],
    REVIEWED: ['prüfung abgeschlossen', 'examen terminé', 'esame completto'],
  };
  searchStrings: string[] = [];
  searchDate?: Date;

  dateControl = new FormControl();

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  add(event: MatChipInputEvent): void {
    if (!this.checkStatus(event.value)) {
      const value = (event.value || '').trim();
      if (this.searchStrings.indexOf(value) !== -1) {
        event.chipInput!.clear();
        return;
      }
      if (value) {
        this.searchStrings.push(value);
      }
    }
    // Clear the input value
    event.chipInput!.clear();
    this.emitSearch({
      searchCriteria: this.searchStrings,
      validOn: this.searchDate,
    });
  }

  // TODO: check this with Hannes
  private checkStatus(searchString: string): boolean {
    const searchStringLower = searchString.toLowerCase();
    for (const [key, value] of Object.entries(this.statusTranslateMapping)) {
      if (value.includes(searchStringLower)) {
        this.searchStrings.push(key);
        return true;
      }
    }
    return false;
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
