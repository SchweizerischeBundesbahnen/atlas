import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';

const routes: Routes = [
  {
    path: '',
    component: TimetableHearingOverviewComponent,
    children: [
      {
        path: Pages.TTFN_DETAIL.path,
        component: RouteToDialogComponent,
        resolve: {},
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
export class TthRoutingModule {}
