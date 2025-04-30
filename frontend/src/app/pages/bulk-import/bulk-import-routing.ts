import { Routes } from '@angular/router';

import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./overview/bulk-import-overview.component').then(
        (m) => m.BulkImportOverviewComponent
      ),
    canDeactivate: [canLeaveDirtyForm],
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./log/bulk-import-log.component').then(
        (m) => m.BulkImportLogComponent
      ),
  },
];
