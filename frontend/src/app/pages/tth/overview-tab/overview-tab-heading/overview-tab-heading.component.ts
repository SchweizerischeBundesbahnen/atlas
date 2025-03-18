import { Component, Input } from '@angular/core';
import { HearingStatus, TimetableHearingYear } from '../../../../api';
import { Pages } from '../../../pages';
import { NgIf } from '@angular/common';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-timetable-hearing-overview-tab-heading',
    templateUrl: './overview-tab-heading.component.html',
    styleUrls: ['./overview-tab-heading.component.scss'],
    imports: [NgIf, DisplayDatePipe, TranslatePipe]
})
export class OverviewTabHeadingComponent {
  @Input() cantonShort!: string;
  @Input() foundTimetableHearingYear!: TimetableHearingYear;
  @Input() hearingStatus!: HearingStatus;
  @Input() noTimetableHearingYearFound!: boolean;
  @Input() noPlannedTimetableHearingYearFound!: boolean;

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
