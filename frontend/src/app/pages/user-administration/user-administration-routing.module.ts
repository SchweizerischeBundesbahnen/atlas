import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { UserAdministrationResolver } from './detail/user-administration.resolver';
import { UserAdministrationComponent } from './detail/user-administration/user-administration.component';
import { Pages } from '../pages';
import { UserAdministrationUserOverviewComponent } from './user/overview/user-administration-overview.component';
import { ClientCredentialAdministrationComponent } from './client-credential/detail/client-credential-administration.component';
import { ClientCredentialAdministrationResolver } from './client-credential/detail/client-credential-administration.resolver';
import { UserAdministrationClientsOverviewComponent } from './client-credential/overview/client-credential-administration-overview.component';

const routes: Routes = [
  {
    path: '',
    component: UserAdministrationOverviewComponent,
    children: [
      {
        path: Pages.USERS.path,
        component: UserAdministrationUserOverviewComponent,
      },
      {
        path: Pages.USERS.path + '/:sbbUserId',
        component: RouteToDialogComponent,
        data: { component: UserAdministrationComponent },
        resolve: {
          user: UserAdministrationResolver,
        },
        runGuardsAndResolvers: 'always',
      },
      {
        path: Pages.CLIENTS.path,
        component: UserAdministrationClientsOverviewComponent,
      },
      {
        path: Pages.CLIENTS.path + '/:clientId',
        component: RouteToDialogComponent,
        data: { component: ClientCredentialAdministrationComponent },
        resolve: {
          clientCredential: ClientCredentialAdministrationResolver,
        },
        runGuardsAndResolvers: 'always',
      },
      { path: '**', redirectTo: Pages.USERS.path },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserAdministrationRoutingModule {}
