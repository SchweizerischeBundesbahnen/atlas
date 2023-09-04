import { Component } from '@angular/core';
import { Cantons } from '../../../core/cantons/Cantons';
import { Canton } from '../../../core/cantons/Canton';

@Component({
  selector: 'app-timetable-hearing-overview',
  templateUrl: './timetable-hearing-overview.component.html',
  styleUrls: ['./timetable-hearing-overview.component.scss'],
})
export class TimetableHearingOverviewComponent {
  get cantons(): Canton[] {
    return Cantons.cantons;
  }

  get swiss(): Canton {
    return Cantons.swiss;
  }
}
