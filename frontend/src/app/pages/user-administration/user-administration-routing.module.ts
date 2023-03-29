import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { UserAdministrationResolver } from './user/detail/user-administration.resolver';
import { UserAdministrationUserDetailComponent } from './user/detail/user-administration/user-administration-user-detail.component';
import { Pages } from '../pages';
import { UserAdministrationUserOverviewComponent } from './user/overview/user-administration-overview.component';
import { UserAdministrationClientDetailComponent } from './client-credential/detail/user-administration-client-detail.component';
import { ClientCredentialAdministrationResolver } from './client-credential/detail/client-credential-administration.resolver';
import { UserAdministrationClientOverviewComponent } from './client-credential/overview/user-administration-client-overview.component';

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
        data: { component: UserAdministrationUserDetailComponent },
        resolve: {
          user: UserAdministrationResolver,
        },
        runGuardsAndResolvers: 'always',
      },
      {
        path: Pages.CLIENTS.path,
        component: UserAdministrationClientOverviewComponent,
      },
      {
        path: Pages.CLIENTS.path + '/:clientId',
        component: RouteToDialogComponent,
        data: { component: UserAdministrationClientDetailComponent },
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
