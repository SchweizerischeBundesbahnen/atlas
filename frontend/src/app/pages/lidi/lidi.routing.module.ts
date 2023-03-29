import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { LineDetailResolver } from './lines/detail/line-detail.resolver';
import { Pages } from '../pages';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { SublineDetailResolver } from './sublines/detail/subline-detail.resolver';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { LinesComponent } from './lines/lines.component';
import { SublinesComponent } from './sublines/sublines.component';
import { LidiWorkflowOverviewComponent } from './workflow/overview/lidi-workflow-overview.component';
import { LineVersionSnapshotResolver } from './workflow/detail/line-version-snapshot.resolver';
import { LineVersionSnapshotDetailComponent } from './workflow/detail/line-version-snapshot-detail.component';

const routes: Routes = [
  {
    path: '',
    component: LidiOverviewComponent,
    children: [
      {
        path: Pages.LINES.path,
        component: LinesComponent,
        children: [
          {
            path: ':id',
            component: RouteToDialogComponent,
            data: { component: LineDetailComponent },
            resolve: {
              lineDetail: LineDetailResolver,
            },
            runGuardsAndResolvers: 'always',
          },
        ],
      },
      {
        path: Pages.SUBLINES.path,
        component: SublinesComponent,
        children: [
          {
            path: ':id',
            component: RouteToDialogComponent,
            data: { component: SublineDetailComponent },
            resolve: {
              sublineDetail: SublineDetailResolver,
            },
            runGuardsAndResolvers: 'always',
          },
        ],
      },
      {
        path: Pages.WORKFLOWS.path,
        component: LidiWorkflowOverviewComponent,
        children: [
          {
            path: ':id',
            component: RouteToDialogComponent,
            data: {
              component: LineVersionSnapshotDetailComponent,
            },
            resolve: {
              lineVersionSnapshot: LineVersionSnapshotResolver,
            },
            runGuardsAndResolvers: 'always',
          },
        ],
      },
      { path: '**', redirectTo: Pages.LINES.path },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LidiRoutingModule {}
