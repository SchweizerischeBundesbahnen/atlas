import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { LineDetailResolver } from './lines/detail/line-detail.resolver';
import { Pages } from '../pages';

const routes: Routes = [
  {
    path: '',
    component: LidiOverviewComponent,
  },
  {
    path: Pages.LINES.path + '/:id',
    component: LineDetailComponent,
    data: { breadcrumb: Pages.LINES.title },
    resolve: {
      lineDetail: LineDetailResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LidiRoutingModule {}
