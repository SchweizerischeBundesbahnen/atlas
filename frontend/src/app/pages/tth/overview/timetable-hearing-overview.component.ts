import { Component, OnInit } from '@angular/core';
import { Cantons } from './canton/Cantons';
import { Canton } from './canton/Canton';
import { TthTableService } from '../tth-table.service';
import { Pages } from '../../pages';

@Component({
  selector: 'app-timetable-hearing-overview',
  templateUrl: './timetable-hearing-overview.component.html',
  styleUrls: ['./timetable-hearing-overview.component.scss'],
})
export class TimetableHearingOverviewComponent implements OnInit {
  constructor(private readonly tthTableService: TthTableService) {}

  ngOnInit() {
    this.tthTableService.activeTabPage = Pages.TTH;
  }

  get cantons(): Canton[] {
    return Cantons.cantons;
  }

  get swiss(): Canton {
    return Cantons.swiss;
  }
}
