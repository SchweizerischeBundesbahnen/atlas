import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { TimetableFieldNumberDetailResolver } from './detail/timetable-field-number-detail.resolver';
import { Pages } from '../pages';

const routes: Routes = [
  {
    path: '',
    component: TimetableFieldNumberOverviewComponent,
  },
  {
    path: Pages.TTFN_DETAIL.path,
    component: TimetableFieldNumberDetailComponent,
    data: { breadcrumb: Pages.TTFN_DETAIL.title },
    resolve: {
      timetableFieldNumberDetail: TimetableFieldNumberDetailResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TtfnRoutingModule {}
