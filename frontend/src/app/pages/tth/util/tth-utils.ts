import { HearingStatus, TimetableHearingYear } from '../../../api';

export class TthUtils {
  static isHearingStatusActive(hearingStatus: HearingStatus): boolean {
    return hearingStatus === HearingStatus.Active;
  }

  static isHearingStatusPlanned(hearingStatus: HearingStatus): boolean {
    return hearingStatus === HearingStatus.Planned;
  }

  static isHearingStatusArchived(hearingStatus: HearingStatus): boolean {
    return hearingStatus === HearingStatus.Archived;
  }

  static sortByTimetableHearingYear(
    timetableHearingYears: TimetableHearingYear[],
    reverse: boolean
  ): TimetableHearingYear[] {
    if (reverse) {
      return timetableHearingYears.sort((n1, n2) => n1.timetableYear - n2.timetableYear).reverse();
    }
    return timetableHearingYears.sort((n1, n2) => n1.timetableYear - n2.timetableYear);
  }
}
