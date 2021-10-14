import moment from 'moment/moment';
import { Injectable } from '@angular/core';

export const DATE_PATTERN = 'DD.MM.yyyy';

export const MIN_DATE: Date = new Date(moment('1900-01-01 00:00:00').valueOf());

export const MAX_DATE: Date = new Date(moment('2099-12-31 23:59:59').valueOf());
export const MAX_DATE_FORMATTED: string = moment(MAX_DATE).format(DATE_PATTERN);

@Injectable({
  providedIn: 'root',
})
export class DateService {
  getCurrentDateFormatted(): string {
    return moment().format(DATE_PATTERN);
  }
}
