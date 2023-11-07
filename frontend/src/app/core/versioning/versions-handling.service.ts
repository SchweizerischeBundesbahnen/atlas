import { Record } from '../components/base-detail/record';
import { DateRange } from './date-range';
import moment from 'moment';

export class VersionsHandlingService {
  static addVersionNumbers<TYPE extends Record>(versions: Array<TYPE>): void {
    versions.forEach((item, index) => (item.versionNumber = index + 1));
  }

  static hasMultipleVersions<TYPE extends Record>(versions: Array<TYPE>): boolean {
    return !!Array.isArray(versions);
  }

  static getMaxValidity<TYPE extends Record>(versions: Array<TYPE>): DateRange {
    VersionsHandlingService.sortByValidFrom(versions);
    return {
      validFrom: versions[0].validFrom!,
      validTo: versions[versions.length - 1].validTo!,
    };
  }

  static sortByValidFrom<TYPE extends Record>(versions: Array<TYPE>): void {
    versions.sort((x, y) => +new Date(x.validFrom!) - +new Date(y.validFrom!));
  }

  static determineDefaultVersionByValidity<TYPE extends Record>(records: Array<TYPE>): TYPE {
    if (records.length == 1) {
      return records[0];
    }
    const now = moment();
    const matchedRecord = this.findRecordByTodayDate(records, now);
    if (matchedRecord.length == 1) {
      return matchedRecord[0];
    } else if (matchedRecord.length > 1) {
      throw new Error('Something went wrong. Found more than one Record.');
    } else if (matchedRecord.length == 0 && records.length > 1) {
      const foundRecordBetweenGap = this.findRecordBetweenGap(records, now);
      if (foundRecordBetweenGap != null) {
        return foundRecordBetweenGap;
      }
      //get next in future
      const firstIndexValidFrom = moment(records[0].validFrom);
      if (now.isBefore(firstIndexValidFrom)) {
        return records[0];
      }
      //get last in passt
      const lastIndexValidTo = moment(records[records.length - 1].validTo);
      if (now.isAfter(lastIndexValidTo)) {
        return records[records.length - 1];
      }
    }
    return records[0];
  }

  private static findRecordByTodayDate<TYPE extends Record>(
    records: Array<TYPE>,
    now: moment.Moment,
  ) {
    return records.filter((record) => {
      const currentValidFrom = moment(record.validFrom);
      const currentValidTo = moment(record.validTo);
      if (currentValidFrom.isSame(currentValidTo, 'day') && now.isSame(currentValidFrom, 'day')) {
        return true;
      }
      return now.isBetween(currentValidFrom, currentValidTo);
    });
  }

  private static findRecordBetweenGap<TYPE extends Record>(
    records: Array<TYPE>,
    now: moment.Moment,
  ) {
    const startRecordsDateRange = records[0].validFrom;
    const endRecordsDateRange = records[records.length - 1].validTo;
    if (now.isBetween(startRecordsDateRange, endRecordsDateRange)) {
      for (let i = 1; i < records.length; i++) {
        const currentValidTo = moment(records[i - 1].validTo);
        const nextValidFrom = moment(records[i].validFrom);
        if (now.isBetween(currentValidTo, nextValidFrom)) {
          return records[i];
        }
      }
    }
    return null;
  }

  static groupVersionsByKey<TYPE extends Record>(
    records: Array<TYPE>,
    groupByKey: string,
  ): Map<string, Array<TYPE>> {
    return records.reduce((group: any, currentData) => {
      const id = (currentData as any)[groupByKey];
      group[id] = [...(group[id] || []), currentData];
      return group;
    }, {});
  }
}
