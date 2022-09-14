import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationCreateComponent } from './create/user-administration-create.component';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { UserAdministrationEditComponent } from './edit/user-administration-edit.component';

const routes: Routes = [
  {
    path: '',
    component: UserAdministrationOverviewComponent,
    children: [
      {
        path: 'add',
        component: RouteToDialogComponent,
        data: { component: UserAdministrationCreateComponent },
      },
      {
        path: ':sbbUserId',
        component: RouteToDialogComponent,
        data: { component: UserAdministrationEditComponent },
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserAdministrationRoutingModule {}
