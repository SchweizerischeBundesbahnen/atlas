import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { TthRoutingModule } from './tth-routing.module';
import { FormModule } from '../../core/module/form.module';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';
import { CantonCardComponent } from './overview/canton-card/canton-card.component';
import { TimetableHearingOverviewDetailComponent } from './timetable-hearing-overview-detail/timetable-hearing-overview-detail.component';
import { TimetableHearingOverviewTabComponent } from './timetable-hearing-overview-tab/timetable-hearing-overview-tab/timetable-hearing-overview-tab.component';

@NgModule({
  declarations: [
    TimetableHearingOverviewComponent,
    CantonCardComponent,
    TimetableHearingOverviewDetailComponent,
    TimetableHearingOverviewTabComponent,
  ],
  imports: [CoreModule, TthRoutingModule, FormModule],
})
export class TthModule {}
