import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { Pages } from './pages/pages';
import { AuthGuard } from './core/auth/guards/auth-guard';
import { AdminGuard } from './core/auth/guards/admin.guard';
import { canActivateTimetableHearing } from './core/auth/guards/timetable-hearing-guard.service';

const routes: Routes = [
  {
    path: Pages.TTFN.path,
    loadChildren: () => import('./pages/ttfn/ttfn.module').then((m) => m.TtfnModule),
    data: { headerTitle: Pages.TTFN.headerTitle },
    canActivate: [AuthGuard],
  },
  {
    path: Pages.LIDI.path,
    loadChildren: () => import('./pages/lidi/lidi.module').then((m) => m.LidiModule),
    data: { headerTitle: Pages.LIDI.headerTitle },
    canActivate: [AuthGuard],
  },
  {
    path: Pages.BODI.path,
    loadChildren: () => import('./pages/bodi/bodi.module').then((m) => m.BodiModule),
    data: { headerTitle: Pages.BODI.headerTitle },
    canActivate: [AuthGuard],
  },
  {
    path: Pages.SEPODI.path,
    loadChildren: () => import('./pages/sepodi/sepodi.module').then((m) => m.SepodiModule),
    data: { headerTitle: Pages.SEPODI.headerTitle },
    canActivate: [AuthGuard],
  },
  {
    path: Pages.TTH.path,
    loadChildren: () => import('./pages/tth/tth.module').then((m) => m.TthModule),
    data: { headerTitle: Pages.TTH.headerTitle },
    canActivate: [canActivateTimetableHearing],
  },
  {
    path: Pages.PRM.path,
    loadChildren: () => import('./pages/prm/prm.module').then((m) => m.PrmModule),
    data: { headerTitle: Pages.SEPODI.headerTitle },
    canActivate: [AuthGuard],
  },
  {
    path: Pages.USER_ADMINISTRATION.path,
    loadChildren: () =>
      import('./pages/user-administration/user-administration.module').then(
        (m) => m.UserAdministrationModule,
      ),
    data: { headerTitle: Pages.USER_ADMINISTRATION.headerTitle },
    canActivate: [AuthGuard, AdminGuard],
  },
  {
    path: Pages.HOME.path,
    component: HomeComponent,
    data: {
      headerTitle: Pages.HOME.headerTitle,
    },
  },
  { path: '**', redirectTo: Pages.HOME.path },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
