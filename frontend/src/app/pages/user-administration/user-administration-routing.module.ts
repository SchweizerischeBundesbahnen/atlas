import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { userResolver } from './user/detail/user-administration.resolver';
import { UserAdministrationUserDetailComponent } from './user/detail/user-administration/user-administration-user-detail.component';
import { Pages } from '../pages';
import { UserAdministrationUserOverviewComponent } from './user/overview/user-administration-overview.component';
import { UserAdministrationClientDetailComponent } from './client-credential/detail/user-administration-client-detail.component';
import { clientCredentialResolver } from './client-credential/detail/client-credential-administration.resolver';
import { UserAdministrationClientOverviewComponent } from './client-credential/overview/user-administration-client-overview.component';

const routes: Routes = [
  {
    path: Pages.USERS.path + '/:sbbUserId',
    component: UserAdministrationUserDetailComponent,
    resolve: {
      user: userResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.CLIENTS.path + '/:clientId',
    component: UserAdministrationClientDetailComponent,
    resolve: {
      clientCredential: clientCredentialResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    component: UserAdministrationOverviewComponent,
    children: [
      {
        path: Pages.USERS.path,
        component: UserAdministrationUserOverviewComponent,
      },
      {
        path: Pages.CLIENTS.path,
        component: UserAdministrationClientOverviewComponent,
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
