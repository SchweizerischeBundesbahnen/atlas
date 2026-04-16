import { Pages } from '../pages';
import { Routes } from '@angular/router';
import { HearingStatus } from '../../api';

export const TTH_CANTON_BO_ROUTES: Routes = [
  {
    path: Pages.TTH_PLANNED.path,
    loadComponent: () =>
      import('./tth-overview-base/tth-overview-base.component').then((m) => m.TthOverviewBaseComponent),
    data: { hearingStatus: HearingStatus.Planned },
    children: [
      { path: '', pathMatch: 'full', redirectTo: Pages.TTH_STATEMENTS.path },
      {
        path: Pages.TTH_STATEMENTS.path,
        loadComponent: () =>
          import('./overview-detail/overview-detail.component').then((m) => m.OverviewDetailComponent),
        data: { hearingStatus: HearingStatus.Planned },
      },
    ],
  },
  {
    path: Pages.TTH_ACTIVE.path,
    loadComponent: () =>
      import('./tth-overview-base/tth-overview-base.component').then((m) => m.TthOverviewBaseComponent),
    data: { hearingStatus: HearingStatus.Active },
    children: [
      { path: '', pathMatch: 'full', redirectTo: Pages.TTH_STATEMENTS.path },
      {
        path: Pages.TTH_STATEMENTS.path,
        loadComponent: () =>
          import('./overview-detail/overview-detail.component').then((m) => m.OverviewDetailComponent),
        data: { hearingStatus: HearingStatus.Active },
      },
      {
        path: Pages.TTH_DOSSIERS.path,
        loadComponent: () =>
          import('./dossier/tth-dossier-overview/tth-dossier-overview.component').then(
            (m) => m.TthDossierOverviewComponent
          ),
        data: { hearingStatus: HearingStatus.Active },
      },
    ],
  },
  {
    path: Pages.TTH_ARCHIVED.path,
    loadComponent: () =>
      import('./tth-overview-base/tth-overview-base.component').then((m) => m.TthOverviewBaseComponent),
    data: { hearingStatus: HearingStatus.Archived },
    children: [
      { path: '', pathMatch: 'full', redirectTo: Pages.TTH_STATEMENTS.path },
      {
        path: Pages.TTH_STATEMENTS.path,
        loadComponent: () =>
          import('./overview-detail/overview-detail.component').then((m) => m.OverviewDetailComponent),
        data: { hearingStatus: HearingStatus.Archived },
      },
      {
        path: Pages.TTH_DOSSIERS.path,
        loadComponent: () =>
          import('./dossier/tth-dossier-overview/tth-dossier-overview.component').then(
            (m) => m.TthDossierOverviewComponent
          ),
        data: { hearingStatus: HearingStatus.Archived },
      },
    ],
  },
  { path: '**', redirectTo: Pages.TTH_ACTIVE.path },
];
