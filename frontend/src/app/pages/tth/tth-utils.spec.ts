import { TestBed } from '@angular/core/testing';
import { TthUtils } from './tth-utils';
import { HearingStatus, TimetableHearingYear } from '../../api';
import moment from 'moment';

describe('TthUtils', () => {
  let util: TthUtils;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    util = TestBed.inject(TthUtils);
  });

  it('should be created', () => {
    expect(util).toBeTruthy();
  });

  it('should return true when HearingStatus is ACTIVE', () => {
    //when
    const result = util.isHearingStatusActive(HearingStatus.Active);
    //then
    expect(result).toBeTruthy();
  });

  it('should return true when HearingStatus is PLANNED', () => {
    //when
    const result = util.isHearingStatusPlanned(HearingStatus.Planned);
    //then
    expect(result).toBeTruthy();
  });

  it('should return true when HearingStatus is ARCHIVED', () => {
    //when
    const result = util.isHearingStatusArchived(HearingStatus.Archived);
    //then
    expect(result).toBeTruthy();
  });

  it('should sort by timetableHearingYear', () => {
    //given
    const timetableHearingYear: TimetableHearingYear[] = [
      {
        timetableYear: 2005,
        hearingFrom: moment().toDate(),
        hearingTo: moment().toDate(),
      },
      {
        timetableYear: 2002,
        hearingFrom: moment().toDate(),
        hearingTo: moment().toDate(),
      },
      {
        timetableYear: 2004,
        hearingFrom: moment().toDate(),
        hearingTo: moment().toDate(),
      },
    ];
    //when
    const result = util.sortByTimetableHearingYear(timetableHearingYear, false);
    //then
    expect(result[0].timetableYear).toBe(2002);
    expect(result[1].timetableYear).toBe(2004);
    expect(result[2].timetableYear).toBe(2005);
  });

  it('should sort reverse by timetableHearingYear', () => {
    //given
    const timetableHearingYear: TimetableHearingYear[] = [
      {
        timetableYear: 2005,
        hearingFrom: moment().toDate(),
        hearingTo: moment().toDate(),
      },
      {
        timetableYear: 2002,
        hearingFrom: moment().toDate(),
        hearingTo: moment().toDate(),
      },
      {
        timetableYear: 2004,
        hearingFrom: moment().toDate(),
        hearingTo: moment().toDate(),
      },
    ];
    //when
    const result = util.sortByTimetableHearingYear(timetableHearingYear, true);
    //then
    expect(result[2].timetableYear).toBe(2002);
    expect(result[1].timetableYear).toBe(2004);
    expect(result[0].timetableYear).toBe(2005);
  });
});
