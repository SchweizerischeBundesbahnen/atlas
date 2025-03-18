import { Component } from '@angular/core';
import { Cantons } from '../../../core/cantons/Cantons';
import { Canton } from '../../../core/cantons/Canton';
import { CantonCardComponent } from './canton-card/canton-card.component';
import { RouterLink } from '@angular/router';
import { NgFor } from '@angular/common';

@Component({
    selector: 'app-timetable-hearing-overview',
    templateUrl: './timetable-hearing-overview.component.html',
    styleUrls: ['./timetable-hearing-overview.component.scss'],
    imports: [CantonCardComponent, RouterLink, NgFor]
})
export class TimetableHearingOverviewComponent {
  get cantons(): Canton[] {
    return Cantons.cantons;
  }

  get swiss(): Canton {
    return Cantons.swiss;
  }
}
