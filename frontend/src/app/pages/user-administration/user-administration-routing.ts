import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { userResolver } from './user/detail/user-administration.resolver';

import { Pages } from '../pages';

import { clientCredentialResolver } from './client-credential/detail/client-credential-administration.resolver';

export const routes: Routes = [
  {
    path: Pages.USERS.path + '/:sbbUserId',
    loadComponent: () =>
      import(
        './user/detail/user-administration/user-administration-user-detail.component'
      ).then((m) => m.UserAdministrationUserDetailComponent),
    resolve: {
      user: userResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.CLIENTS.path + '/:clientId',
    loadComponent: () =>
      import(
        './client-credential/detail/user-administration-client-detail.component'
      ).then((m) => m.UserAdministrationClientDetailComponent),
    resolve: {
      clientCredential: clientCredentialResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    loadComponent: () =>
      import('./overview/user-administration-overview.component').then(
        (m) => m.UserAdministrationOverviewComponent
      ),
    children: [
      {
        path: Pages.USERS.path,
        loadComponent: () =>
          import('./user/overview/user-administration-overview.component').then(
            (m) => m.UserAdministrationUserOverviewComponent
          ),
      },
      {
        path: Pages.CLIENTS.path,
        loadComponent: () =>
          import(
            './client-credential/overview/user-administration-client-overview.component'
          ).then((m) => m.UserAdministrationClientOverviewComponent),
      },
      { path: '**', redirectTo: Pages.USERS.path },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserAdministrationRouting {}
