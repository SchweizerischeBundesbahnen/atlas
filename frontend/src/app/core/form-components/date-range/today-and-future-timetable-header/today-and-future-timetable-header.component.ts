import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MatDatepicker} from "@angular/material/datepicker";
import moment, {Moment} from "moment/moment";
import {TimetableYearChangeService} from "../../../../api";

@Component({
    selector: 'today-and-future-timetable-header',
    templateUrl: 'today-and-future-timetable-header.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TodayAndFutureTimetableHeaderComponent {

  constructor(private timetableYearChangeService: TimetableYearChangeService,
              private datepicker: MatDatepicker<Moment>) {
  }

  selectToday() {
    this.selectPredefinedDate(moment());
  }

  selectFutureTimetable() {
    this.timetableYearChangeService.getNextTimetablesYearChange(1).subscribe(dates => {
      this.selectPredefinedDate(moment(dates[0]));
    })
  }

  selectPredefinedDate(date: Moment) {
    this.datepicker.select(date.startOf('day'));
    this.datepicker.close();
  }

}
