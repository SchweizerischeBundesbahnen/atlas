import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { lineResolver } from './lines/detail/line-detail.resolver';
import { Pages } from '../pages';

import { sublineResolver } from './sublines/detail/subline-detail.resolver';

import { lineVersionSnapshotResolver } from './workflow/detail/line-version-snapshot.resolver';

import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

export const routes: Routes = [
  {
    path: Pages.LINES.path + '/:id',
    loadComponent: () =>
      import('./lines/detail/line-detail.component').then(
        (m) => m.LineDetailComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      lineDetail: lineResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.SUBLINES.path + '/:id',
    loadComponent: () =>
      import('./sublines/detail/subline-detail.component').then(
        (m) => m.SublineDetailComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      sublineDetail: sublineResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.WORKFLOWS.path + '/:id',
    loadComponent: () =>
      import('./workflow/detail/line-version-snapshot-detail.component').then(
        (m) => m.LineVersionSnapshotDetailComponent
      ),
    resolve: {
      lineVersionSnapshot: lineVersionSnapshotResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    loadComponent: () =>
      import('./overview/lidi-overview.component').then(
        (m) => m.LidiOverviewComponent
      ),
    children: [
      {
        path: Pages.LINES.path,
        loadComponent: () =>
          import('./lines/lines.component').then((m) => m.LinesComponent),
      },
      {
        path: Pages.WORKFLOWS.path,
        loadComponent: () =>
          import('./workflow/overview/lidi-workflow-overview.component').then(
            (m) => m.LidiWorkflowOverviewComponent
          ),
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
