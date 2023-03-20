import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';
import { TimetableHearingOverviewDetailComponent } from './timetable-hearing-overview-detail/timetable-hearing-overview-detail.component';

const routes: Routes = [
  {
    path: '',
    component: TimetableHearingOverviewComponent,
  },
  {
    path: Pages.TTH_OVERVIEW_DETAIL.path,
    component: TimetableHearingOverviewDetailComponent,
    runGuardsAndResolvers: 'always',
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TthRoutingModule {}
