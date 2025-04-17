import { Record } from '../components/base-detail/record';
import moment from 'moment';
import { VersionsHandlingService } from './versions-handling.service';

describe('VersionsHandlingService', () => {
  const firstRecord: Record = {
    id: 1,
    validFrom: moment('1.1.2000', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2000', 'DD.MM.YYYY').toDate(),
  };
  const secondRecord: Record = {
    id: 2,
    validFrom: moment('1.1.2001', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2001', 'DD.MM.YYYY').toDate(),
  };
  const thirdRecord: Record = {
    id: 3,
    validFrom: moment('1.1.2002', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2002', 'DD.MM.YYYY').toDate(),
  };

  it('should return the firstRecord version when today is the firstRecord range', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2000', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(firstRecord.id);
  });

  it('should return the secondRecord version when today is the secondRecord range', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2001', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(secondRecord.id);
  });

  it('should return the thirdRecord version when today is the thirdRecord range', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2002', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(thirdRecord.id);
  });

  it('should return the firstRecord version when today is before all records', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.1999', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(firstRecord.id);
  });

  it('should return the thirdRecord version when today is after all records', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2099', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(thirdRecord.id);
  });

  it('should return the thirdRecord version when today is after all records', () => {
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const fourthRecord: Record = {
      id: 4,
      validFrom: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2004', 'DD.MM.YYYY').toDate(),
    };
    records.push(fourthRecord);
    //given
    const today = moment('1.2.2003', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(fourthRecord.id);
  });

  it('should return the record version with today', () => {
    const todayRecord: Record = {
      id: 1,
      validFrom: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const tomorrowRecord: Record = {
      id: 2,
      validFrom: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const records: Array<Record> = [todayRecord, tomorrowRecord];
    //given
    const today = moment('1.1.2004', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record =
      VersionsHandlingService.determineDefaultVersionByValidity(records);

    //then
    expect(record.id).toBe(todayRecord.id);
  });

  it('should return the first temporal sorted valid from of records', () => {
    //given
    const first: Record = {
      id: 1,
      validFrom: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const second: Record = {
      id: 2,
      validFrom: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const third: Record = {
      id: 3,
      validFrom: moment('4.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('4.1.2004', 'DD.MM.YYYY').toDate(),
    };

    //when
    const result = VersionsHandlingService.getMaxValidity([
      first,
      second,
      third,
    ]);

    //then
    expect(result.validFrom).toEqual(moment('1.1.2004', 'DD.MM.YYYY').toDate());
  });

  it('should return the last temporal sorted valid to', () => {
    //given
    const first: Record = {
      id: 1,
      validFrom: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const second: Record = {
      id: 2,
      validFrom: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const third: Record = {
      id: 3,
      validFrom: moment('4.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('4.1.2004', 'DD.MM.YYYY').toDate(),
    };

    //when
    const result = VersionsHandlingService.getMaxValidity([
      first,
      second,
      third,
    ]);

    //then
    expect(result.validTo).toEqual(moment('4.1.2004', 'DD.MM.YYYY').toDate());
  });
});
