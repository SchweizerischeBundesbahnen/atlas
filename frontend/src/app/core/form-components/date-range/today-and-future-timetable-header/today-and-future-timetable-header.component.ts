import { Component, inject } from '@angular/core';
import { MatCalendarHeader, MatDatepicker } from '@angular/material/datepicker';
import moment, { Moment } from 'moment/moment';
import { TimetableYearChangeInternalService } from '../../../../api/service/lidi/timetable-year-change-internal.service';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-today-and-future-timetable-header',
  templateUrl: 'today-and-future-timetable-header.component.html',
  imports: [MatButton, MatCalendarHeader, TranslatePipe],
})
export class TodayAndFutureTimetableHeaderComponent {
  private readonly timetableYearChangeService = inject(TimetableYearChangeInternalService);
  private readonly datepicker = inject(MatDatepicker<Moment>);

  selectToday() {
    this.selectPredefinedDate(moment());
  }

  selectFutureTimetable() {
    this.timetableYearChangeService.getNextTimetablesYearChange(1).subscribe((dates) => {
      this.selectPredefinedDate(moment(dates[0]));
    });
  }

  selectPredefinedDate(date: Moment) {
    this.datepicker.select(date.startOf('day'));
    this.datepicker.close();
  }
}
