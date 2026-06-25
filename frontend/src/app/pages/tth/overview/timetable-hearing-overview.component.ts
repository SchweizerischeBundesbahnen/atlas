import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Cantons } from '../../../core/cantons/Cantons';
import { Canton } from '../../../core/cantons/Canton';
import { CantonCardComponent } from './canton-card/canton-card.component';
import { RouterLink } from '@angular/router';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';

@Component({
  selector: 'atlas-timetable-hearing-overview',
  templateUrl: './timetable-hearing-overview.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./timetable-hearing-overview.component.scss'],
  imports: [CantonCardComponent, RouterLink],
})
export class TimetableHearingOverviewComponent {
  private readonly overviewToTabService = inject(OverviewToTabShareDataService);
  get cantons(): Canton[] {
    return Cantons.cantons;
  }

  get swiss(): Canton {
    return Cantons.swiss;
  }

  onCantonCardClick(cantonShort: string) {
    this.overviewToTabService.setCantonShort(cantonShort);
  }
}
