import { Component, Input } from '@angular/core';
import { TimetableHearingYear } from '../../../../api';
import { Pages } from '../../../pages';

@Component({
  selector: 'app-timetable-hearing-overview-tab-heading',
  templateUrl: './timetable-hearing-overview-tab-heading.component.html',
  styleUrls: ['./timetable-hearing-overview-tab-heading.component.scss'],
})
export class TimetableHearingOverviewTabHeadingComponent {
  @Input() cantonShort!: string;
  @Input() foundTimetableHearingYear!: TimetableHearingYear;
  @Input() hearingPlan!: string;

  isHearingPlanActual() {
    return this.hearingPlan === Pages.TTH_ACTIVE.path;
  }

  isHearingPlanPlanned() {
    return this.hearingPlan === Pages.TTH_PLANNED.path;
  }

  isHearingPlanArchived() {
    return this.hearingPlan === Pages.TTH_ARCHIVED.path;
  }
}
