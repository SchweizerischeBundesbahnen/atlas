import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';
import { ServicePointDetailComponent } from './service-points/service-point-detail.component';

const routes: Routes = [
  {
    path: ':id',
    component: ServicePointDetailComponent,
  },
  {
    path: '',
    component: SepodiOverviewComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SepodiRoutingModule {}
