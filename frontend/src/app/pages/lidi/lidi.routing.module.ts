import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { LineDetailResolver } from './lines/detail/line-detail.resolver';
import { Pages } from '../pages';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { SublineDetailResolver } from './sublines/detail/subline-detail.resolver';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';

const routes: Routes = [
  {
    path: '',
    component: LidiOverviewComponent,
    children: [
      {
        path: Pages.LINES.path + '/:id',
        component: RouteToDialogComponent,
        data: { component: LineDetailComponent },
        resolve: {
          lineDetail: LineDetailResolver,
        },
        runGuardsAndResolvers: 'always',
      },
      {
        path: Pages.SUBLINES.path + '/:id',
        component: RouteToDialogComponent,
        data: { component: SublineDetailComponent },
        resolve: {
          sublineDetail: SublineDetailResolver,
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
export class LidiRoutingModule {}
