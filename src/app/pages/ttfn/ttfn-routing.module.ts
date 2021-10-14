import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { TimetableFieldNumberDetailResolver } from './detail/timetable-field-number-detail.resolver';

const routes: Routes = [
  {
    path: '',
    component: TimetableFieldNumberOverviewComponent,
  },
  {
    path: ':id',
    component: TimetableFieldNumberDetailComponent,
    data: { breadcrumb: 'PAGES.DETAILS' },
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
