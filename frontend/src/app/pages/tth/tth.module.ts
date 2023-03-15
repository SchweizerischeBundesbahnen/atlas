import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { TthRoutingModule } from './tth-routing.module';
import { FormModule } from '../../core/module/form.module';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';

@NgModule({
  declarations: [TimetableHearingOverviewComponent],
  imports: [CoreModule, TthRoutingModule, FormModule],
})
export class TthModule {}
