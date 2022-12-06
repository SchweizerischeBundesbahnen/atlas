import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { LidiWorkflowOverviewComponent } from './overview/lidi-workflow-overview.component';
import { LidiRoutingModule } from './lidi-workflow.routing.module';

@NgModule({
  declarations: [LidiWorkflowOverviewComponent],
  imports: [CoreModule, LidiRoutingModule],
})
export class LidiWorkflowModule {}
