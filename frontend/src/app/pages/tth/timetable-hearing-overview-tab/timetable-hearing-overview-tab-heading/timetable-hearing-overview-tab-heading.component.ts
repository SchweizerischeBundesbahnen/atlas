import { Component, Input } from '@angular/core';
import { HearingStatus, TimetableHearingYear } from '../../../../api';
import { Pages } from '../../../pages';

@Component({
  selector: 'app-timetable-hearing-overview-tab-heading',
  templateUrl: './timetable-hearing-overview-tab-heading.component.html',
  styleUrls: ['./timetable-hearing-overview-tab-heading.component.scss'],
})
export class TimetableHearingOverviewTabHeadingComponent {
  @Input() cantonShort!: string;
  @Input() foundTimetableHearingYear!: TimetableHearingYear;
  @Input() hearingStatus!: HearingStatus;
  @Input() noActiveTimetableHearingYearFound!: boolean;

  get isHearingStatusActive() {
    return this.hearingStatus.toLowerCase() === Pages.TTH_ACTIVE.path;
  }

  get isHearingStatusPlanned() {
    return this.hearingStatus.toLowerCase() === Pages.TTH_PLANNED.path;
  }

  get isHearingStatusArchived() {
    return this.hearingStatus.toLowerCase() === Pages.TTH_ARCHIVED.path;
  }
}
