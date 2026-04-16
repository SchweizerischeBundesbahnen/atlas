import { Component, inject, Input } from '@angular/core';
import { HearingStatus, TimetableHearingYear } from '../../../../api';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { NgOptimizedImage } from '@angular/common';
import { OverviewToTabShareDataService } from '../service/overview-to-tab-share-data.service';

@Component({
  selector: 'atlas-timetable-hearing-overview-tab-heading',
  templateUrl: './overview-tab-heading.component.html',
  styleUrls: ['./overview-tab-heading.component.scss'],
  imports: [DisplayDatePipe, TranslatePipe, NgOptimizedImage],
})
export class OverviewTabHeadingComponent {
  @Input() cantonShort!: string;
  @Input() foundTimetableHearingYear!: TimetableHearingYear;
  @Input() hearingStatus!: HearingStatus;
  @Input() isTimetableHearingYearFound!: boolean;
  @Input() isPlannedTimetableHearingYearFound!: boolean;

  overviewToTabShareDataService = inject(OverviewToTabShareDataService);

  readonly isHearingYearActive = this.overviewToTabShareDataService.isHearingYearActive;
  readonly isHearingYearPlanned = this.overviewToTabShareDataService.isHearingYearPlanned;
  readonly isHearingYearArchived = this.overviewToTabShareDataService.isHearingYearArchived;
}
