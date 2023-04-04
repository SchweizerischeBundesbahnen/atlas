import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';
import { TimetableHearingOverviewTabComponent } from './timetable-hearing-overview-tab/timetable-hearing-overview-tab.component';
import { TimetableHearingOverviewDetailComponent } from './timetable-hearing-overview-detail/timetable-hearing-overview-detail.component';
import { HearingStatus } from '../../api';

const routes: Routes = [
  {
    path: '',
    component: TimetableHearingOverviewComponent,
  },
  {
    path: Pages.TTH_OVERVIEW_DETAIL.path,
    component: TimetableHearingOverviewTabComponent,
    children: [
      {
        path: Pages.TTH_ACTIVE.path,
        component: TimetableHearingOverviewDetailComponent,
        data: {
          hearingStatus: HearingStatus.Active,
        },
      },
      {
        path: Pages.TTH_PLANNED.path,
        component: TimetableHearingOverviewDetailComponent,
        data: {
          hearingStatus: HearingStatus.Planned,
        },
      },
      {
        path: Pages.TTH_ARCHIVED.path,
        component: TimetableHearingOverviewDetailComponent,
        data: {
          hearingStatus: HearingStatus.Archived,
        },
      },
      { path: '**', redirectTo: Pages.TTH_ACTIVE.path },
    ],
  },
  { path: '**', redirectTo: Pages.TTH.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TthRoutingModule {}
