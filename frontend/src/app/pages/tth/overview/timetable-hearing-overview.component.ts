import { Component } from '@angular/core';
import { Cantons } from './canton/Cantons';
import { Canton } from './canton/Canton';

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
