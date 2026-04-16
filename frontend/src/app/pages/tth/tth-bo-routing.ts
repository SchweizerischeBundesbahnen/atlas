import { Routes } from '@angular/router';
import { Pages } from '../pages';
import { HearingStatus } from '../../api';

export const TTH_BO_ROUTES: Routes = [
  {
    path: Pages.TTH_ACTIVE.path,
    loadComponent: () =>
      import('./tth-overview-base/tth-overview-base.component').then((m) => m.TthOverviewBaseComponent),
    data: { hearingStatus: HearingStatus.Active },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: Pages.TTH_DOSSIERS.path,
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
  { path: '**', redirectTo: Pages.TTH_ACTIVE.path },
];
