import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { LidiOverviewComponent } from './overview/lidi-overview.component';

const routes: Routes = [
  {
    path: Pages.LIDI.path,
    component: LidiOverviewComponent,
    data: {
      breadcrumb: Pages.LIDI.title,
    },
    // children: [
    // {
    //   path: ':id',
    //   component: BehigDetailComponent,
    //   data: { breadcrumb: 'BREADCRUMB.DETAIL' },
    //   resolve: { behigData: BehigDetailResolver },
    //   runGuardsAndResolvers: 'paramsOrQueryParamsChange',
    // },
    // ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LidiRoutingModule {}
