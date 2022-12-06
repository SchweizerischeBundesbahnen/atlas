import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Pages } from '../pages';
import { LidiWorkflowOverviewComponent } from './overview/lidi-workflow-overview.component';

const routes: Routes = [
  {
    path: '',
    component: LidiWorkflowOverviewComponent,
  },
  { path: '**', redirectTo: Pages.LIDI_WORKFLOW.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LidiRoutingModule {}
