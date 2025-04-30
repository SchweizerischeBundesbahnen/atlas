import { Routes } from '@angular/router';

import { timetableFieldNumberResolver } from './detail/timetable-field-number-detail.resolver';
import { Pages } from '../pages';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

export const routes: Routes = [
  {
    path: Pages.TTFN_DETAIL.path,
    loadComponent: () =>
      import('./detail/timetable-field-number-detail.component').then(
        (m) => m.TimetableFieldNumberDetailComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      timetableFieldNumberDetail: timetableFieldNumberResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    loadComponent: () =>
      import('./overview/timetable-field-number-overview.component').then(
        (m) => m.TimetableFieldNumberOverviewComponent
      ),
  },
  { path: '**', redirectTo: '' },
];
