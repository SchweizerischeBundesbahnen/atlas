import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { LineDetailResolver } from './lines/detail/line-detail.resolver';

const routes: Routes = [
  {
    path: '',
    component: LidiOverviewComponent,
  },
  {
    path: 'lines/:id',
    component: LineDetailComponent,
    data: { breadcrumb: 'PAGES.DETAILS' },
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
