import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';

const routes: Routes = [
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
