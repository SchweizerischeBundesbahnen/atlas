import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { TtfnRoutingModule } from './ttfn-routing.module';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';

@NgModule({
  declarations: [TimetableFieldNumberOverviewComponent, TimetableFieldNumberDetailComponent],
  imports: [CoreModule, TtfnRoutingModule],
})
export class TtfnModule {}
