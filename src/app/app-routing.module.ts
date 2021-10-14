import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { Pages } from './pages/pages';

const routes: Routes = [
  {
    path: Pages.TTFN.path,
    loadChildren: () => import('./pages/ttfn/ttfn.module').then((m) => m.TtfnModule),
    data: { breadcrumb: Pages.TTFN.title },
  },
  {
    path: Pages.LIDI.path,
    loadChildren: () => import('./pages/lidi/lidi.module').then((m) => m.LidiModule),
    data: { breadcrumb: Pages.LIDI.title },
  },
  {
    path: Pages.HOME.path,
    component: HomeComponent,
    data: {
      breadcrumb: Pages.HOME.title,
    },
  },
  { path: '**', redirectTo: Pages.HOME.path },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
