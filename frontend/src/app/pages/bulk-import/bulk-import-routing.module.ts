import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BulkImportOverviewComponent } from './overview/bulk-import-overview.component';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { BulkImportLogComponent } from './log/bulk-import-log.component';
import {loggedInUsers} from "../../core/auth/guards/auth-guard";

const routes: Routes = [
  {
    path: '',
    component: BulkImportOverviewComponent,
    canDeactivate: [canLeaveDirtyForm],
    canActivate: [loggedInUsers],
    runGuardsAndResolvers: 'always',
  },
  {
    path: ':id',
    component: BulkImportLogComponent,
    runGuardsAndResolvers: 'always',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BulkImportRoutingModule {}
