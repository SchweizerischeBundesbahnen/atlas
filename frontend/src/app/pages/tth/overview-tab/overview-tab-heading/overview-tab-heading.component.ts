import { ChangeDetectionStrategy, Component, inject, Input, input } from '@angular/core';
import { HearingStatus, TimetableHearingYear } from '../../../../api';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { NgOptimizedImage } from '@angular/common';
import { OverviewToTabShareDataService } from '../service/overview-to-tab-share-data.service';

@Component({
  selector: 'atlas-timetable-hearing-overview-tab-heading',
  templateUrl: './overview-tab-heading.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./overview-tab-heading.component.scss'],
  imports: [DisplayDatePipe, TranslatePipe, NgOptimizedImage],
})
export class OverviewTabHeadingComponent {
  private readonly overviewToTabShareDataService = inject(OverviewToTabShareDataService);

  @Input() cantonShort!: string;

  @Input() foundTimetableHearingYear!: TimetableHearingYear;

  @Input() hearingStatus!: HearingStatus;
  readonly isTimetableHearingYearFound = input.required<boolean>();
  readonly isPlannedTimetableHearingYearFound = input.required<boolean>();

  readonly isHearingYearActive = this.overviewToTabShareDataService.isHearingYearActive;
  readonly isHearingYearPlanned = this.overviewToTabShareDataService.isHearingYearPlanned;
  readonly isHearingYearArchived = this.overviewToTabShareDataService.isHearingYearArchived;
}
