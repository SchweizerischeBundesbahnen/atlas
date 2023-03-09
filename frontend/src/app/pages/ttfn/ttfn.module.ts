import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { TtfnRoutingModule } from './ttfn-routing.module';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { FormModule } from '../../core/module/form.module';
import { AtlasSearchSelectModule } from '../../core/form-components/atlas-search-select/atlas-search-select.module';

@NgModule({
  declarations: [TimetableFieldNumberOverviewComponent, TimetableFieldNumberDetailComponent],
  imports: [CoreModule, TtfnRoutingModule, FormModule, AtlasSearchSelectModule],
})
export class TtfnModule {}
