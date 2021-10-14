import { NgModule } from '@angular/core';
import { CoreModule, withBasePath } from '../../core/module/core.module';
import { TtfnRoutingModule } from './ttfn-routing.module';
import { environment } from '../../../environments/environment';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { TtfnApiModule } from '../../api/ttfn';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [TimetableFieldNumberOverviewComponent, TimetableFieldNumberDetailComponent],
  imports: [
    CoreModule,
    TtfnRoutingModule,
    TtfnApiModule.forRoot(withBasePath(environment.ttfnBackendUrl)),
  ],
})
export class TtfnModule {}
