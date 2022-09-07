import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationCreateComponent } from './create/user-administration-create.component';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';

// TODO: check security with AuthGuards
const routes: Routes = [
  { path: '', component: UserAdministrationOverviewComponent },
  {
    path: 'add',
    component: RouteToDialogComponent,
    data: { component: UserAdministrationCreateComponent },
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserAdministrationRoutingModule {}
