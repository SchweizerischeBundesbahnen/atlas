import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { lineResolver } from './lines/detail/line-detail.resolver';
import { Pages } from '../pages';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { sublineResolver } from './sublines/detail/subline-detail.resolver';
import { LinesComponent } from './lines/lines.component';
import { SublinesComponent } from './sublines/sublines.component';
import { LidiWorkflowOverviewComponent } from './workflow/overview/lidi-workflow-overview.component';
import { lineVersionSnapshotResolver } from './workflow/detail/line-version-snapshot.resolver';
import { LineVersionSnapshotDetailComponent } from './workflow/detail/line-version-snapshot-detail.component';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

const routes: Routes = [
  {
    path: Pages.LINES.path + '/:id',
    component: LineDetailComponent,
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      lineDetail: lineResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.SUBLINES.path + '/:id',
    component: SublineDetailComponent,
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      sublineDetail: sublineResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.WORKFLOWS.path + '/:id',
    component: LineVersionSnapshotDetailComponent,
    resolve: {
      lineVersionSnapshot: lineVersionSnapshotResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    component: LidiOverviewComponent,
    children: [
      {
        path: Pages.LINES.path,
        component: LinesComponent,
      },
      {
        path: Pages.SUBLINES.path,
        component: SublinesComponent,
      },
      {
        path: Pages.WORKFLOWS.path,
        component: LidiWorkflowOverviewComponent,
      },
      { path: '**', redirectTo: Pages.LINES.path },
    ],
  },
  { path: '**', redirectTo: Pages.LIDI.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LidiRoutingModule {}
