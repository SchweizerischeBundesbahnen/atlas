import moment from 'moment';
import { Injectable } from '@angular/core';

export const DATE_PATTERN = 'DD.MM.yyyy';
export const DATE_TIME_FORMAT = 'DD.MM.yyyy HH:mm:ss';

export const MIN_DATE: Date = new Date(moment('1700-01-01 00:00:00').valueOf());

export const MAX_DATE: Date = new Date(moment('9999-12-31 23:59:59').valueOf());
export const MAX_DATE_FORMATTED: string = moment(MAX_DATE).format(DATE_PATTERN);

@Injectable({
  providedIn: 'root',
})
export class DateService {
  static getDateFormatted(date: Date | undefined) {
    return moment(date).format(DATE_PATTERN);
  }

  static differenceInDays(first: Date, second: Date): number {
    return moment(second).diff(moment(first), 'days');
  }

  getCurrentDateFormatted(): string {
    return moment().format(DATE_PATTERN);
  }
}
