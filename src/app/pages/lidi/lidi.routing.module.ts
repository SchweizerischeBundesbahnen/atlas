import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { LineDetailResolver } from './lines/detail/line-detail.resolver';

export const LINE_DETAILS_PATH = Pages.LIDI.path + '/lines';

const routes: Routes = [
  {
    path: Pages.LIDI.path,
    component: LidiOverviewComponent,
    data: {
      breadcrumb: Pages.LIDI.title,
    },
  },
  {
    path: LINE_DETAILS_PATH + '/:id',
    component: LineDetailComponent,
    data: { breadcrumb: 'PAGES.DETAILS' },
    resolve: {
      lineDetail: LineDetailResolver,
    },
    runGuardsAndResolvers: 'always',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LidiRoutingModule {}
