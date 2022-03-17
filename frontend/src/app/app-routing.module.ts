import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { Pages } from './pages/pages';

const routes: Routes = [
  {
    path: Pages.TTFN.path,
    loadChildren: () => import('./pages/ttfn/ttfn.module').then((m) => m.TtfnModule),
    data: { headerTitle: Pages.TTFN.headerTitle },
  },
  {
    path: Pages.LIDI.path,
    loadChildren: () => import('./pages/lidi/lidi.module').then((m) => m.LidiModule),
    data: { headerTitle: Pages.LIDI.headerTitle },
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
