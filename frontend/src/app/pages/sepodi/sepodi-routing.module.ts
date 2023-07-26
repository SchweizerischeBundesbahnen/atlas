import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';
import { ServicePointDetailComponent } from './service-points/service-point-detail.component';
import { Pages } from '../pages';
import { servicePointResolver } from './service-points/service-point-detail.resolver';

const routes: Routes = [
  {
    path: '',
    component: SepodiOverviewComponent,
    children: [
      {
        path: Pages.SERVICE_POINTS.path + '/:id',
        component: ServicePointDetailComponent,
        resolve: { servicePoint: servicePointResolver },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SepodiRoutingModule {}
