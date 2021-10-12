import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { TimetableFieldNumberDetailResolver } from './detail/timetable-field-number-detail.resolver';

const routes: Routes = [
  {
    path: Pages.TTFN.path,
    component: TimetableFieldNumberOverviewComponent,
    data: {
      breadcrumb: Pages.TTFN.title,
    },
  },
  {
    path: Pages.TTFN.path + '/:id',
    component: TimetableFieldNumberDetailComponent,
    data: { breadcrumb: 'PAGES.DETAILS' },
    resolve: {
      timetableFieldNumberDetail: TimetableFieldNumberDetailResolver,
    },
    runGuardsAndResolvers: 'always',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TtfnRoutingModule {}
