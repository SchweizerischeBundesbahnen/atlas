import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { UserAdministrationResolver } from './user-administration.resolver';
import { UserAdministrationComponent } from './detail/user-administration/user-administration.component';

const routes: Routes = [
  {
    path: '',
    component: UserAdministrationOverviewComponent,
    children: [
      {
        path: ':sbbUserId',
        component: RouteToDialogComponent,
        data: { component: UserAdministrationComponent },
        resolve: {
          user: UserAdministrationResolver,
        },
        runGuardsAndResolvers: 'always',
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
