import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { TimetableFieldNumberDetailResolver } from './detail/timetable-field-number-detail.resolver';
import { Pages } from '../pages';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';

const routes: Routes = [
  {
    path: '',
    component: TimetableFieldNumberOverviewComponent,
    children: [
      {
        path: Pages.TTFN_DETAIL.path,
        component: RouteToDialogComponent,
        data: { component: TimetableFieldNumberDetailComponent },
        resolve: {
          timetableFieldNumberDetail: TimetableFieldNumberDetailResolver,
        },
        runGuardsAndResolvers: 'always',
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TtfnRoutingModule {}
