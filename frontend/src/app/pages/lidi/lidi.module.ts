import {NgModule} from '@angular/core';
import {CoreModule} from '../../core/module/core.module';
import {LinesComponent} from './lines/lines.component';
import {LidiRoutingModule} from './lidi.routing.module';
import {LidiOverviewComponent} from './overview/lidi-overview.component';
import {LineDetailComponent} from './lines/detail/line-detail.component';
import {SublineDetailComponent} from './sublines/detail/subline-detail.component';
import {FormModule} from '../../core/module/form.module';
import {MainlineSelectOptionPipe} from './sublines/detail/mainline-select-option.pipe';
import {LidiWorkflowOverviewComponent} from './workflow/overview/lidi-workflow-overview.component';
import {LineVersionSnapshotDetailComponent} from './workflow/detail/line-version-snapshot-detail.component';
import {LineDetailFormComponent} from './lines/detail/line-detail-form/line-detail-form.component';

@NgModule({
  declarations: [
    LineVersionSnapshotDetailComponent,
    LidiWorkflowOverviewComponent,
    LineDetailFormComponent,
    LidiOverviewComponent,
    LinesComponent,
    LineDetailComponent,
    SublineDetailComponent,
    MainlineSelectOptionPipe,
  ],
  imports: [CoreModule, LidiRoutingModule, FormModule],
})
export class LidiModule {}
