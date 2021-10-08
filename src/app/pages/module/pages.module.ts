import { NgModule } from '@angular/core';
import { AuthInsightsComponent } from '../auth-insights/auth-insights.component';
import { HomeComponent } from '../home/home.component';
import { TimetableFieldNumberDetailComponent } from '../timetable-field-number-detail/timetable-field-number-detail.component';
import { CoreModule, withBasePath } from '../../core/module/core.module';
import { LidiModule } from '../lidi/lidi.module';
import { TtfnApiModule } from '../../api/ttfn';
import { environment } from '../../../environments/environment';

@NgModule({
  declarations: [AuthInsightsComponent, HomeComponent, TimetableFieldNumberDetailComponent],
  imports: [
    CoreModule,
    LidiModule,
    TtfnApiModule.forRoot(withBasePath(environment.ttfnBackendUrl)),
  ],
})
export class PagesModule {}
