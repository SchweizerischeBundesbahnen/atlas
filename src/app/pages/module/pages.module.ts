import { NgModule } from '@angular/core';
import { AuthInsightsComponent } from '../auth-insights/auth-insights.component';
import { HomeComponent } from '../home/home.component';
import { TimetableFieldNumberDetailComponent } from '../timetable-field-number-detail/timetable-field-number-detail.component';
import { CoreModule } from '../../core/module/core.module';

@NgModule({
  declarations: [AuthInsightsComponent, HomeComponent, TimetableFieldNumberDetailComponent],
  imports: [CoreModule],
})
export class PagesModule {}
