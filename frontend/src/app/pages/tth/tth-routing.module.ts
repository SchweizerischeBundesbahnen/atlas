import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';
import { OverviewTabComponent } from './overview-tab/overview-tab.component';
import { OverviewDetailComponent } from './overview-detail/overview-detail.component';
import { HearingStatus } from '../../api';
import { StatementDetailComponent } from './statement/statement-detail.component';
import { statementResolver } from './statement/statement-detail.resolver';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

const statementActiveDetailPath = `${Pages.TTH_OVERVIEW_DETAIL.path}/${Pages.TTH_ACTIVE.path}/${Pages.TTH_STATEMENT_DETAILS.path}`;
const statementPlannedDetailPath = `${Pages.TTH_OVERVIEW_DETAIL.path}/${Pages.TTH_PLANNED.path}/${Pages.TTH_STATEMENT_DETAILS.path}`;
const statementArchivedDetailPath = `${Pages.TTH_OVERVIEW_DETAIL.path}/${Pages.TTH_ARCHIVED.path}/${Pages.TTH_STATEMENT_DETAILS.path}`;

const routes: Routes = [
  {
    path: '',
    component: TimetableHearingOverviewComponent,
  },
  {
    path: statementActiveDetailPath,
    component: StatementDetailComponent,
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
    component: StatementDetailComponent,
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
    component: StatementDetailComponent,
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
    component: OverviewTabComponent,
    children: [
      {
        path: Pages.TTH_ACTIVE.path,
        component: OverviewDetailComponent,
        data: {
          hearingStatus: HearingStatus.Active,
        },
      },
      {
        path: Pages.TTH_PLANNED.path,
        component: OverviewDetailComponent,
        data: {
          hearingStatus: HearingStatus.Planned,
        },
      },
      {
        path: Pages.TTH_ARCHIVED.path,
        component: OverviewDetailComponent,
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
export class TthRoutingModule {}
