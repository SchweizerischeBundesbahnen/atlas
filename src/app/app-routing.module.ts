import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { Pages } from './pages/pages';

const routes: Routes = [
  {
    path: Pages.HOME.path,
    component: HomeComponent,
    data: {
      breadcrumb: Pages.HOME.title,
    },
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
