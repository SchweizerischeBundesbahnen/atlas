import { Injectable } from '@angular/core';
import { HearingStatus, TimetableHearingYear } from '../../../api';

@Injectable({
  providedIn: 'root',
})
export class TthUtils {
  isHearingStatusActive(hearingStatus: HearingStatus): boolean {
    return hearingStatus === HearingStatus.Active;
  }

  isHearingStatusPlanned(hearingStatus: HearingStatus): boolean {
    return hearingStatus === HearingStatus.Planned;
  }

  isHearingStatusArchived(hearingStatus: HearingStatus): boolean {
    return hearingStatus === HearingStatus.Archived;
  }

  sortByTimetableHearingYear(
    timetableHearingYears: TimetableHearingYear[],
    reverse: boolean
  ): TimetableHearingYear[] {
    if (reverse) {
      return timetableHearingYears.sort((n1, n2) => n1.timetableYear - n2.timetableYear).reverse();
    }
    return timetableHearingYears.sort((n1, n2) => n1.timetableYear - n2.timetableYear);
  }
}
