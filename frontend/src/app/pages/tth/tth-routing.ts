import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';

import { HearingStatus } from '../../api';

import { statementResolver } from './statement/statement-detail.resolver';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

const statementActiveDetailPath = `${Pages.TTH_OVERVIEW_DETAIL.path}/${Pages.TTH_ACTIVE.path}/${Pages.TTH_STATEMENT_DETAILS.path}`;
const statementPlannedDetailPath = `${Pages.TTH_OVERVIEW_DETAIL.path}/${Pages.TTH_PLANNED.path}/${Pages.TTH_STATEMENT_DETAILS.path}`;
const statementArchivedDetailPath = `${Pages.TTH_OVERVIEW_DETAIL.path}/${Pages.TTH_ARCHIVED.path}/${Pages.TTH_STATEMENT_DETAILS.path}`;

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./overview/timetable-hearing-overview.component').then(
        (m) => m.TimetableHearingOverviewComponent
      ),
  },
  {
    path: statementActiveDetailPath,
    loadComponent: () =>
      import('./statement/statement-detail.component').then(
        (m) => m.StatementDetailComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      statement: statementResolver,
    },
    data: {
      hearingStatus: HearingStatus.Active,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: statementPlannedDetailPath,
    loadComponent: () =>
      import('./statement/statement-detail.component').then(
        (m) => m.StatementDetailComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      statement: statementResolver,
    },
    data: {
      hearingStatus: HearingStatus.Planned,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: statementArchivedDetailPath,
    loadComponent: () =>
      import('./statement/statement-detail.component').then(
        (m) => m.StatementDetailComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      statement: statementResolver,
    },
    data: {
      hearingStatus: HearingStatus.Archived,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.TTH_OVERVIEW_DETAIL.path,
    loadComponent: () =>
      import('./overview-tab/overview-tab.component').then(
        (m) => m.OverviewTabComponent
      ),
    children: [
      {
        path: Pages.TTH_ACTIVE.path,
        loadComponent: () =>
          import('./overview-detail/overview-detail.component').then(
            (m) => m.OverviewDetailComponent
          ),
        data: {
          hearingStatus: HearingStatus.Active,
        },
      },
      {
        path: Pages.TTH_PLANNED.path,
        loadComponent: () =>
          import('./overview-detail/overview-detail.component').then(
            (m) => m.OverviewDetailComponent
          ),
        data: {
          hearingStatus: HearingStatus.Planned,
        },
      },
      {
        path: Pages.TTH_ARCHIVED.path,
        loadComponent: () =>
          import('./overview-detail/overview-detail.component').then(
            (m) => m.OverviewDetailComponent
          ),
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
export class TthRouting {}
