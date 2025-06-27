import { Routes } from '@angular/router';

import { userResolver } from './user/detail/user-administration-user-detail-resolver.service';

import { Pages } from '../pages';

import { clientCredentialResolver } from './client-credential/detail/client-credential-administration.resolver';
import { UserPermissionProviderService } from '../../core/components/permissions/application-permission/user-permission-provider-service';
import { UserPermissionGivenUserService } from './user/detail/edit/user-permission-given-user.service';
import { UserPermissionGivenClientService } from './client-credential/detail/edit/user-permission-given-client.service';

export const routes: Routes = [
  {
    path: Pages.USERS.path + '/:sbbUserId',
    loadComponent: () =>
      import('./user/detail/user-administration-user-detail.component').then(
        (m) => m.UserAdministrationUserDetailComponent
      ),
    resolve: {
      user: userResolver,
    },
    providers: [
      {
        provide: UserPermissionProviderService,
        useExisting: UserPermissionGivenUserService,
      },
    ],
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
    providers: [
      {
        provide: UserPermissionProviderService,
        useExisting: UserPermissionGivenClientService,
      },
    ],
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
          import(
            './user/overview/user-administration-user-overview.component'
          ).then((m) => m.UserAdministrationUserOverviewComponent),
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
